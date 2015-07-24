package com.mycontacts;

import android.app.Activity;
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

public class ConfirmDeleteActivity extends Activity implements OnClickListener {

	Button btnConfDelete, btnConfCancel;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_delete);

		btnConfDelete = (Button) findViewById(R.id.btnConfDelete);
		btnConfDelete.setOnClickListener(this);
		btnConfCancel = (Button) findViewById(R.id.btnConfCancel);
		btnConfCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		Intent intenttoconfirm = new Intent();
		// ContentValues cv = new ContentValues();
		DBHelper dbHelper = new DBHelper(this);

		// подключаемся к БД
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor c = db.query("mycontacts", null, null, null, null, null, null);

		switch (v.getId()) {

		case R.id.btnConfCancel:
			setResult(RESULT_CANCELED, intenttoconfirm);
			finish();
			break;

		case R.id.btnConfDelete:

			int position = ChangeAccountActivity.position;
			
			Log.d("myLogs", "Поступил запрос на удаление строки по id = " + position);
			if (c.moveToPosition(position)) {
				int getRowId = c.getColumnIndex("id");
				if (Integer.toString(position).equalsIgnoreCase("")) {
					break;
				}
				Log.d("myLogs", "--- Удаление строки из mycontacts: --- по id = " + position);
				Log.d("myLogs", "--- Плюс id в БД = " + Integer.parseInt(c.getString(getRowId)));
				// удаляем по id из DB
				int delCount = db.delete("mycontacts","id = " + Integer.parseInt(c.getString(getRowId)), null);
				Log.d("myLogs", "Удалено строк = " + delCount + " по id " + position + 
						" id in DB = " + Integer.parseInt(c.getString(getRowId)));
				Log.d("myLogs", "Delete from Main.Names on pos= " + position);
				MainActivity.Names.remove(position);
				setResult(-1, intenttoconfirm);
				c.close();
				super.finish();
			}
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
