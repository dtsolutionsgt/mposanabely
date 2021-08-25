package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_usgrupoopcObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_usgrupoopc";
    private String sql;
    public ArrayList<clsClasses.clsP_usgrupoopc> items= new ArrayList<clsClasses.clsP_usgrupoopc>();

    public clsP_usgrupoopcObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
        cont=context;
        Con=dbconnection;
        ins=Con.Ins;upd=Con.Upd;
        db = dbase;
        count = 0;
    }

    public void reconnect(BaseDatos dbconnection, SQLiteDatabase dbase) {
        Con=dbconnection;
        ins=Con.Ins;upd=Con.Upd;
        db = dbase;
    }

    public void add(clsClasses.clsP_usgrupoopc item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_usgrupoopc item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_usgrupoopc item) {
        deleteItem(item);
    }

    public void delete(String id) {
        deleteItem(id);
    }

    public void fill() {
        fillItems(sel);
    }

    public void fill(String specstr) {
        fillItems(sel+ " "+specstr);
    }

    public void fillSelect(String sq) {
        fillItems(sq);
    }

    public clsClasses.clsP_usgrupoopc first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_usgrupoopc item) {

        ins.init("P_usgrupoopc");

        ins.add("GRUPO",item.grupo);
        ins.add("OPCION",item.opcion);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_usgrupoopc item) {

        upd.init("P_usgrupoopc");


        upd.Where("(GRUPO='"+item.grupo+"') AND (OPCION="+item.opcion+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_usgrupoopc item) {
        sql="DELETE FROM P_usgrupoopc WHERE (GRUPO='"+item.grupo+"') AND (OPCION="+item.opcion+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM P_usgrupoopc WHERE id='" + id+"'";

        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_usgrupoopc item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_usgrupoopc();

            item.grupo=dt.getString(0);
            item.opcion=dt.getInt(1);

            items.add(item);

            dt.moveToNext();
        }

        if (dt!=null) dt.close();

    }

    public int newID(String idsql) {
        Cursor dt=null;
        int nid;

        try {
            dt=Con.OpenDT(idsql);
            dt.moveToFirst();
            nid=dt.getInt(0)+1;
        } catch (Exception e) {
            nid=1;
        }

        if (dt!=null) dt.close();

        return nid;
    }

    public String addItemSql(clsClasses.clsP_usgrupoopc item) {

        ins.init("P_usgrupoopc");

        ins.add("GRUPO",item.grupo);
        ins.add("OPCION",item.opcion);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_usgrupoopc item) {

        upd.init("P_usgrupoopc");


        upd.Where("(GRUPO='"+item.grupo+"') AND (OPCION="+item.opcion+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

