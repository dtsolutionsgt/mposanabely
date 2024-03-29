package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_usgrupoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_usgrupo";
    private String sql;
    public ArrayList<clsClasses.clsP_usgrupo> items= new ArrayList<clsClasses.clsP_usgrupo>();

    public clsP_usgrupoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_usgrupo item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_usgrupo item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_usgrupo item) {
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

    public clsClasses.clsP_usgrupo first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_usgrupo item) {

        ins.init("P_usgrupo");

        ins.add("CODIGO",item.codigo);
        ins.add("NOMBRE",item.nombre);
        ins.add("CUENTA",item.cuenta);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_usgrupo item) {

        upd.init("P_usgrupo");

        upd.add("NOMBRE",item.nombre);
        upd.add("CUENTA",item.cuenta);

        upd.Where("(CODIGO='"+item.codigo+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_usgrupo item) {
        sql="DELETE FROM P_usgrupo WHERE (CODIGO='"+item.codigo+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM P_usgrupo WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_usgrupo item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_usgrupo();

            item.codigo=dt.getString(0);
            item.nombre=dt.getString(1);
            item.cuenta=dt.getString(2);

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

    public String addItemSql(clsClasses.clsP_usgrupo item) {

        ins.init("P_usgrupo");

        ins.add("CODIGO",item.codigo);
        ins.add("NOMBRE",item.nombre);
        ins.add("CUENTA",item.cuenta);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_usgrupo item) {

        upd.init("P_usgrupo");

        upd.add("NOMBRE",item.nombre);
        upd.add("CUENTA",item.cuenta);

        upd.Where("(CODIGO='"+item.codigo+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

