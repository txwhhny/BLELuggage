package com.style.bleluggage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Administrator
 * @version Version1.0
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "blconfig.db";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "tblConfiguration";
	public final static String PARAMID = "ParamId";
	public final static String PARAMTIP = "ParamTip";
	public final static String PARAMVALUE = "ParamValue";

	public final static int IDX_BOX_MAC = 0;
	public final static int IDX_LOC1_MAC = 1;
	public final static int IDX_LOC2_MAC = 2;
	public final static int IDX_LOC3_MAC = 3;
	public final static int IDX_SEND_INTERVAL = 4;
	public final static int IDX_MAX = 5;

	private String [] mstrValues;

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DatabaseHelper(Context ctx)
	{
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		mstrValues = new String[IDX_MAX];
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String strCmd = "create table " + TABLE_NAME + "(" + PARAMID +" integer primary key not null," + PARAMTIP + " text, " + PARAMVALUE + " text not null);";
		db.execSQL(strCmd);
		createData(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	private void createData(SQLiteDatabase db)
	{
		ContentValues cv = new ContentValues();

		cv.put(PARAMID, IDX_BOX_MAC);
		cv.put(PARAMTIP, "Box Mac");
		cv.put(PARAMVALUE, "00:15:83:00:72:80");
		db.insert(TABLE_NAME, null, cv);

		cv.put(PARAMID, IDX_LOC1_MAC);
		cv.put(PARAMTIP, "Locate1 Mac");
		cv.put(PARAMVALUE, "AC:C7:0A:80:4B:52");
		db.insert(TABLE_NAME, null, cv);

		cv.put(PARAMID, IDX_LOC2_MAC);
		cv.put(PARAMTIP, "Locate2 Mac");
		cv.put(PARAMVALUE, "EB:6B:2A:1F:BA:6C");
		db.insert(TABLE_NAME, null, cv);

		cv.put(PARAMID, IDX_LOC3_MAC);
		cv.put(PARAMTIP, "Locate3 Mac");
		cv.put(PARAMVALUE, "AC:C7:0A:80:4B:5F");
		db.insert(TABLE_NAME, null, cv);

		cv.put(PARAMID, IDX_SEND_INTERVAL);
		cv.put(PARAMTIP, "Send Interval");
		cv.put(PARAMVALUE, "300");
		db.insert(TABLE_NAME, null, cv);
	}

	private boolean updateData(int id, String strValue)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String strCond = PARAMID + " = ?";
		String[] strCondValue = { Integer.toString(id) };

		ContentValues cv = new ContentValues();
		cv.put(PARAMVALUE, strValue);
		return db.update(TABLE_NAME, cv, strCond, strCondValue) > 0;
	}

	// Step 1.
	public void initData()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor result = db.query(TABLE_NAME, new String[]{PARAMVALUE}, "", null, null, null, PARAMID);

		for (int i = 0; i < IDX_MAX && result.moveToNext(); i++)
		{
			mstrValues[i] = result.getString(result.getColumnIndex(PARAMVALUE));
		}
	}

	// Step n.
	// ...

	/**
	 *
	 * @param iNum 要获取的基站MAC对应的ID。
	 * @return 基站MAC字符串
     */
	public String getDevMac(int iNum)
	{
		if (iNum < IDX_BOX_MAC || iNum > IDX_LOC3_MAC)		return "";
		return mstrValues[iNum];
	}

	public boolean setDevMac(int iNum, String strMac)
	{
		if (iNum < IDX_BOX_MAC || iNum > IDX_LOC3_MAC)		return false;
		if (updateData(iNum, strMac))
		{
			mstrValues[iNum] = strMac;
			return true;
		}
		return false;
	}

	public boolean setSendInterval(int iInterval)
	{
		if (updateData(IDX_SEND_INTERVAL, "" + iInterval))
		{
			mstrValues[IDX_SEND_INTERVAL] = "" + iInterval;
			return true;
		}
		return false;
	}

	public int getSendInterval()
	{
		return Integer.parseInt(mstrValues[IDX_SEND_INTERVAL]);
	}
}
