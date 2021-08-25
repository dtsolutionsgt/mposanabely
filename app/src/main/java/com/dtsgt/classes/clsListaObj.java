package com.dtsgt.classes;

import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.Optional;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsListaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM View";
    private String sql;
    public ArrayList<clsClasses.clsLista> items = new ArrayList<clsClasses.clsLista>();

    public clsListaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
        cont = context;
        Con = dbconnection;
        ins = Con.Ins;
        upd = Con.Upd;
        db = dbase;
        count = 0;
    }

    public void reconnect(BaseDatos dbconnection, SQLiteDatabase dbase) {
        Con = dbconnection;
        ins = Con.Ins;
        upd = Con.Upd;
        db = dbase;
    }

    public void add(clsClasses.clsLista item) {
        addItem(item);
    }

    public void update(clsClasses.clsLista item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsLista item) {
        deleteItem(item);
    }

    public void delete(int id) {
        deleteItem(id);
    }

    public void fill() {
        fillItems(sel,0);
    }

    public void fill(String specstr) {
        fillItems(sel + " " + specstr,0);
    }

    public void fillSelect(String sq,int valid) {
        fillItems(sq, valid);
    }

    public clsClasses.clsLista first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsLista item) {

        ins.init("View");

        ins.add("PK", item.pk);
        ins.add("F1", item.f1);
        ins.add("F2", item.f2);
        ins.add("F3", item.f3);
        ins.add("F4", item.f4);
        ins.add("F5", item.f5);
        ins.add("F6", item.f6);
        ins.add("F7", item.f7);
        ins.add("F8", item.f8);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsLista item) {

        upd.init("View");

        upd.add("F1", item.f1);
        upd.add("F2", item.f2);
        upd.add("F3", item.f3);
        upd.add("F4", item.f4);
        upd.add("F5", item.f5);
        upd.add("F6", item.f6);
        upd.add("F7", item.f7);
        upd.add("F8", item.f8);

        upd.Where("(PK=" + item.pk + ")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsLista item) {
        sql = "DELETE FROM View WHERE (PK=" + item.pk + ")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql = "DELETE FROM View WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq, int valid) {
        Cursor dt;
        clsClasses.clsLista item;
        int rangoI,rangoF;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsLista();

            item.pk = dt.getInt(0);
            if(valid==20){
                rangoI = dt.getInt(9);
                rangoF = dt.getInt(10);
                item.f1 ="Serie: "+ dt.getString(1)+",   Del "+rangoI+" Hasta "+rangoF;
                if(dt.getString(2).equals("P")) item.f2 = "Pagos"; else item.f2="Correlativo Desconocido";
            }else {
                item.f1 = dt.getString(1);
                item.f2 = dt.getString(2);
            }
            item.f3 = dt.getString(3);
            item.f4 = dt.getString(4);
            item.f5 = dt.getString(5);
            item.f6 = dt.getString(6);
            item.f7 = dt.getString(7);
            item.f8 = dt.getString(8);

            items.add(item);

            dt.moveToNext();
        }

    }

    public int newID(String idsql) {
        Cursor dt;
        int nid;

        try {
            dt = Con.OpenDT(idsql);
            dt.moveToFirst();
            nid = dt.getInt(0) + 1;
        } catch (Exception e) {
            nid = 1;
        }

        return nid;
    }

    public String addItemSql(clsClasses.clsLista item) {

        ins.init("View");

        ins.add("PK", item.pk);
        ins.add("F1", item.f1);
        ins.add("F2", item.f2);
        ins.add("F3", item.f3);
        ins.add("F4", item.f4);
        ins.add("F5", item.f5);
        ins.add("F6", item.f6);
        ins.add("F7", item.f7);
        ins.add("F8", item.f8);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsLista item) {

        upd.init("View");

        upd.add("F1", item.f1);
        upd.add("F2", item.f2);
        upd.add("F3", item.f3);
        upd.add("F4", item.f4);
        upd.add("F5", item.f5);
        upd.add("F6", item.f6);
        upd.add("F7", item.f7);
        upd.add("F8", item.f8);

        upd.Where("(PK=" + item.pk + ")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

