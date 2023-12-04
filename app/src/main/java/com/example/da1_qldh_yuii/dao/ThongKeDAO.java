package com.example.da1_qldh_yuii.dao;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.da1_qldh_yuii.database.DbHelper;
import com.example.da1_qldh_yuii.model.SanPham;

import java.util.ArrayList;

public class ThongKeDAO {

    DbHelper dbHelper;

    public ThongKeDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    public ArrayList<SanPham> getAll() {
        ArrayList<SanPham> list = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT ct.maSanPham, COUNT(ct.maSanPham), SUM(ct.soLuong) as SoLuong FROM CHITIETHOADON ct, SANPHAM sp WHERE ct.maSanPham = sp.maSanPham GROUP BY ct.maSanPham", null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

            do {
                list.add(new SanPham(cursor.getString(0),  cursor.getInt(1),cursor.getInt(2)));
            } while (cursor.moveToNext());
        }

        return list;
    }

    public ArrayList<SanPham> getTop5() {
        ArrayList<SanPham> list = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT ct.maSanPham, COUNT(ct.maSanPham), SUM(ct.soLuong) as SoLuong FROM CHITIETHOADON ct, SANPHAM sp WHERE ct.maSanPham = sp.maSanPham GROUP BY ct.maSanPham ORDER BY SoLuong DESC LIMIT 5", null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

            do {
                list.add(new SanPham(cursor.getString(0),  cursor.getInt(1),cursor.getInt(2)));
            } while (cursor.moveToNext());
        }

        return list;
    }

    public int getTongSoLuong(){
        int tong = 0;
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT SUM(ct.soLuong) as TongSoLuong FROM CHITIETHOADON ct", null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

            do {
                tong += cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return tong;
    }

    public int getTongSoLuongTop5(){
        int tong = 0;
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT SUM(ct.soLuong) as TongSoLuong, ct.maSanPham FROM CHITIETHOADON ct GROUP BY ct.maSanPham ORDER BY TongSoLuong DESC LIMIT 5", null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

            do {
                tong += cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return tong;
    }

    public int getTongDonHang(){
        int tong = 0;
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT count(maHoaDon) as tonghoadon FROM HOADON", null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

            do {
                tong += cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return tong;
    }

    public int getDHDaGiao(int status){
        int tong = 0;
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String[] selectionArgs = new String[]{String.valueOf(status)};
        Cursor cursor = database.rawQuery("SELECT count(maHoaDon) FROM HOADON WHERE trangThai = ?", selectionArgs);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                tong += cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return tong;
    }


    public long getDoanhThu() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String query = "SELECT SUM(bg.giaBan * ct.soLuong) as TongDoanhThu " +
                "FROM CHITIETHOADON ct " +
                "JOIN HOADON hd ON ct.maHoaDon = hd.maHoaDon " +
                "JOIN SANPHAM sp ON ct.maSanPham = sp.maSanPham " +
                "JOIN BANGGIA bg ON sp.maBangGia = bg.maBangGia " +
                "WHERE hd.trangThai = 1";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") long totalRevenue = cursor.getLong(cursor.getColumnIndex("TongDoanhThu"));
            Log.d(TAG, "getDoanhThu: " + totalRevenue);
            return totalRevenue;
        } else {
            return 0;
        }
    }
}
