package com.mycontacts;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChangeAccountActivity extends Activity implements OnClickListener {

	public static final String SHOWITEMINTENT_EXTRA_FETCHROWID = "fetchRow";
	public static final int ACTIVITY_SHOWITEM = 0;

	EditText fname, lname, phone;

	Button btnDelete, btnCancel, btnSave;

	Intent intenttomain, intenttoconfirm, intent;

	public static int position;

	String intfname, intlname, intphone;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.existing_account);

		fname = (EditText) findViewById(R.id.etfirstname);
		lname = (EditText) findViewById(R.id.etlastname);
		phone = (EditText) findViewById(R.id.etphone);

		btnSave = (Button) findViewById(R.id.ExAcbtnsave);
		btnSave.setOnClickListener(this);

		btnCancel = (Button) findViewById(R.id.ExAcbtncancel);
		btnCancel.setOnClickListener(this);

		btnDelete = (Button) findViewById(R.id.ExAcbtndelete);
		btnDelete.setOnClickListener(this);

		intent = getIntent();
		position = intent.getIntExtra("position", 0);
		Log.d("myLogs", "--- Get ID from MAIN: --- " + position);
		intfname = intent.getStringExtra("fname");
		intlname = intent.getStringExtra("lname");
		intphone = intent.getStringExtra("phone");

		fname.setText(intfname);
		lname.setText(intlname);
		phone.setText(intphone);

	}

	@Override
	public void onClick(View v) {

		ContentValues cv = new ContentValues();
		DBHelper dbHelper = new DBHelper(this);

		// подключаемся к БД
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor c = db.query("mycontacts", null, null, null, null, null, null);
		switch (v.getId()) {

		case R.id.ExAcbtnsave:

			if (Integer.toString(position).equalsIgnoreCase("")) {
				break;
			}
			if(c.moveToPosition(position)){
			int idColIndex = c.getColumnIndex("id");
			Log.d("myLogs", "--- Update mycontacts: ---");
			// подготовим значения для обновления
			cv.put("fname", fname.getText().toString());
			cv.put("lname", lname.getText().toString());
			cv.put("phone", phone.getText().toString());
			// обновляем по id
			int updCount = db.update("mycontacts", cv, "id = ?",
					new String[] { Integer.toString(c.getInt(idColIndex)) });
			Log.d("myLogs", "updated rows count = " + updCount + " in id = "
					+ Integer.toString(c.getInt(idColIndex))+ " position " + position);
			intenttomain = new Intent();
			intenttomain.putExtra("position", position);
			intenttomain.putExtra("fname", fname.getText().toString());
			intenttomain.putExtra("lname", lname.getText().toString());
			intenttomain.putExtra("phone", phone.getText().toString());
			setResult(2, intenttomain);
			finish();
			}
			break;

		case R.id.ExAcbtncancel:
			setResult(RESULT_CANCELED, intenttomain);
			finish();
			break;
		case R.id.ExAcbtndelete:
			intenttoconfirm = new Intent(this, ConfirmDeleteActivity.class);
			// intenttoconfirm.putExtra("position", position);
			Log.d("myLogs", "ExAcbtndelete: Удалить контакт на позиции = "
					+ position);
			startActivityForResult(intenttoconfirm, 1);
			// finish();
			break;
		default:
			break;
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		Log.d("myLogs", "ChangeAccActiv: requestCode = " + requestCode
				+ "; resultCode = " + resultCode);
		switch (resultCode) {
		case -1:
			intenttomain = new Intent();
			//MainActivity.Names.remove(position);
			setResult(0, intenttomain);
			super.finish();
			break;
		case 0:
			//finish();
			break;

		default:
			break;
		}
		
	}

	class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			// конструктор суперкласса
			super(context, "myDB1", null, 2);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d("myLogs", "--- onCreate database ---");
			// создаем таблицу с полями
			db.execSQL("create table mycontacts ("
					+ "id integer primary key autoincrement," + "fname text,"
					+ "lname text," + "phone text" + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}
}
