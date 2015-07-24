package com.mycontacts;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
//import android.content.ContentValues;
//import android.os.Build;
//import android.os.Handler;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener {

	public static final String SHOWITEMINTENT_EXTRA_FETCHROWID = "fetchRow";
	public static final int ACTIVITY_SHOWITEM = 0; /* Intent request user index */

	public static ArrayList<String> Names = new ArrayList<>();
	Button btnAdd;
	DBHelper dbHelper;

	public static int id = 0;

	Intent intenttomain;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		btnAdd = (Button) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(this);

		// находим список
		ListView lvMain = (ListView) findViewById(R.id.lvMain);

		// создаем адаптер и присваиваем адаптер списку
		lvMain.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, Names));
		lvMain.setOnItemLongClickListener(this);
		lvMain.setOnItemClickListener(this);
		
		Names.clear();
		LoadDBToList();
    	Log.d("myLogs", "Хотим записать след строку по ID in Main = " + id);
	}

	private void LoadDBToList() {
		Log.d("myLogs", "LoadDBToList()");
		// создаем объект для создания и управления версиями БД
				dbHelper = new DBHelper(this);

				// подключаемся к БД
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				Cursor c = db.query("mycontacts", null, null, null, null, null, null);
				// ставим позицию курсора на строку выборки id
				// если в выборке нет строк, вернется false
	
				if (c.moveToFirst()) {
					// определяем номера столбцов по имени в выборке
					int idColIndex = c.getColumnIndex("id");
					int fnameColIndex = c.getColumnIndex("fname");
					int lnameColIndex = c.getColumnIndex("lname");
					int phoneColIndex = c.getColumnIndex("phone");
					
					do { 
						Log.d("myLogs", "Заполняем строку Names под id = " + c.getPosition());
						Names.add(c.getPosition(), c.getString(fnameColIndex) + " "
								+ c.getString(lnameColIndex) + "\n"
								+ c.getString(phoneColIndex));
						// получаем значения по номерам столбцов и пишем все в лог
						Log.d("myLogs",
								"ID = " + c.getInt(idColIndex) + ", fname = "
										+ c.getString(fnameColIndex) + ", lname = "
										+ c.getString(lnameColIndex) + ", phone = "
										+ c.getString(phoneColIndex));
						// переход на следующую строку
						// а если следующей нет (текущая - последняя), то false -
						// выходим из цикла
						id = c.getInt(idColIndex)+1;
					} while (c.moveToNext());
				} else
					Log.d("myLogs", "Нет строк в БД!");
				c.close();
	}

	public void onItemClick(AdapterView<?> Av, View v, int position, long id) {

		// Log.d("myLogs", "--- Get ID from MAIN: --- " + position);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor c = db.query("mycontacts", null, null, null, null, null, null);
		if (c.moveToPosition(position)) {
			// определяем номера столбцов по имени в выборке
			int fnameColIndex = c.getColumnIndex("fname");
			int lnameColIndex = c.getColumnIndex("lname");
			int phoneColIndex = c.getColumnIndex("phone");

			intenttomain = new Intent(this, ChangeAccountActivity.class);
			intenttomain.putExtra("position", position);
			intenttomain.putExtra("fname", c.getString(fnameColIndex));
			intenttomain.putExtra("lname", c.getString(lnameColIndex));
			intenttomain.putExtra("phone", c.getString(phoneColIndex));
			startActivityForResult(intenttomain, 21);
		} else
			Log.d("myLogs", "0 rows");
		c.close();

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
			long id) {
//		 Toast.makeText(this,
//                 "Item in position " + position + " long clicked",
//                 Toast.LENGTH_LONG).show();
		 
		 	dbHelper = new DBHelper(this);
		 	
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			
			Cursor c = db.query("mycontacts", null, null, null, null, null, null);

			if (c.moveToPosition(position)) {
				// определяем номера столбцов по имени в выборке
				int idColIndex = c.getColumnIndex("id");
				int fnameColIndex = c.getColumnIndex("fname");
				int lnameColIndex = c.getColumnIndex("lname");
				int phoneColIndex = c.getColumnIndex("phone");
				
					Log.d("myLogs", "Хотим набрать контакт с id = " + c.getPosition());
					// получаем значения по номерам столбцов и пишем все в лог
					Log.d("myLogs",
							"ID = " + c.getInt(idColIndex) + ", fname = "
									+ c.getString(fnameColIndex) + ", lname = "
									+ c.getString(lnameColIndex) + ", phone = "
									+ c.getString(phoneColIndex));
					
					Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + c.getString(phoneColIndex)));
					startActivity(intentCall);
 
			} else
				Log.d("myLogs", "Несуществующий контакт!");
			c.close();
		return true;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnAdd:
			
			Intent intent = new Intent(this, CreateNewAccountActivity.class);
			startActivityForResult(intent, 1);
			break;

		default:
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		Log.d("myLogs", "MainActiv: requestCode = " + requestCode + "; resultCode = " + resultCode);
//		Handler handler = new Handler();
		String fname = data.getStringExtra("fname");
		String lname = data.getStringExtra("lname");
		String phone = data.getStringExtra("phone");
		switch (resultCode) {
		case 1:
			Log.d("myLogs", "--- data get string extra ---" + fname + " " + lname + " " + phone + " id= " +id);
			Names.add(id, fname + " " + lname + "\n" + phone);
			
//			  handler.postDelayed(new Runnable()
//			  {
//			    @Override
//			    public void run()
//			    {
//			      Log.d("TX.refresh", "Switching to %s from %s");
//			      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
//			      {
//			        finish();
//			        startActivity(getIntent());
//			      } else recreate();
//			    }
//			  }, 1);
			 // reload();
			break;
		case -1:
			int pos = data.getIntExtra("position", 1);
			MainActivity.Names.set(pos, fname + " " + lname + "\n"	+ phone);
//			  handler.postDelayed(new Runnable()
//			  {
//			    @Override
//			    public void run()
//			    {
//			      Log.d("TX.refresh", "Switching to %s from %s");
//			      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
//			      {
//			        finish();
//			        startActivity(getIntent());
//			      } else recreate();
//			    }
//			  }, 1);
			//reload();
		case RESULT_CANCELED:
//			  handler.postDelayed(new Runnable()
//			  {
//			    @Override
//			    public void run()
//			    {
//			      Log.d("TX.refresh", "Switching to %s from %s");
//			      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
//			      {
//			        finish();
//			        startActivity(getIntent());
//			      } else recreate();
//			    }
//			  }, 1);
			//recreate();
			  //reload();
			break;
		case 2:
			int position = data.getIntExtra("position", 1);
			MainActivity.Names.set(position, fname + " " + lname + "\n"	+ phone);
//			  handler.postDelayed(new Runnable()
//			  {
//			    @Override
//			    public void run()
//			    {
//			      Log.d("TX.refresh", "Switching to %s from %s");
//			      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
//			      {
//			        finish();
//			        startActivity(getIntent());
//			      } else recreate();
//			    }
//			  }, 1);
			//recreate();
			 // reload();
			break;
		default:
			break;
		}
		reload();
	}

	
	@SuppressLint("NewApi")
	public void reload()
    {
//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        finish();
//        startActivity(intent);
		if (Build.VERSION.SDK_INT >= 11) {
		    recreate();
		} else {
		    Intent intent = getIntent();
		    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		    finish();
		    overridePendingTransition(0, 0);

		    startActivity(intent);
		    overridePendingTransition(0, 0);
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
