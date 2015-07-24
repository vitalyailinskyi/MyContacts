package com.mycontacts;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CreateNewAccountActivity extends Activity implements
		OnClickListener {

	EditText etFName, etLName, etPhone;
	Button btnSave, btnCancel;
	DBHelper dbHelper;
	
	String fname, lname, phone;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_new_account);

		btnCancel = (Button) findViewById(R.id.btncancel);
		btnCancel.setOnClickListener(this);

		btnSave = (Button) findViewById(R.id.btnsave);
		btnSave.setOnClickListener(this);

		etFName = (EditText) findViewById(R.id.etfirstname);
		etLName = (EditText) findViewById(R.id.etlastname);
		etPhone = (EditText) findViewById(R.id.etphone);

		// создаем объект для создания и управления версиями БД
		dbHelper = new DBHelper(this);
	}

	@Override
	public void onClick(View v) {
		// создаем объект для данных
		ContentValues cv = new ContentValues();
		Intent intent = new Intent();
		// получаем данные из полей ввода
		fname = etFName.getText().toString();
		lname = etLName.getText().toString();
		phone = etPhone.getText().toString();

		// подключаемся к БД
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (v.getId()) {
		case R.id.btnsave:

			Log.d("myLogs", "--- Insert in mycontacts: --- to ID = " + MainActivity.id);
			cv.put("id", MainActivity.id);
			cv.put("fname", fname);
			cv.put("lname", lname);
			cv.put("phone", phone);
			// вставляем запись и получаем ее ID
			long rowId = db.insert("mycontacts", null, cv);
			Log.d("myLogs", "В БД ЗАПИСАЛАСЬ НОВАЯ СТРОКА, ID = " + rowId);
			MainActivity.id++;
			intent.putExtra("fname", fname.toString());
			intent.putExtra("lname", lname.toString());
			intent.putExtra("phone", phone.toString());
			Log.d("myLogs", "Intent " + fname + " "+ lname + " "+ phone + " then finish");
			setResult(0, intent);
			finish();
			break;

		case R.id.btncancel:
//			int delCount = db.delete("mycontacts", null, null);
//			Log.d("myLogs", "deleted rows count = " + delCount + " in id " + MainActivity.id);
//			MainActivity.Names.clear();
//			MainActivity.id = 0;
			setResult(RESULT_CANCELED, intent);
			finish();
			break;
		default:
			break;
		}
		// закрываем подключение к БД
		dbHelper.close();
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
					+ "lname text," + "phone long" + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}
}
