package com.dtsgt.classes;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_cajacierreObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_cajacierre";
    private String sql;
    public ArrayList<clsClasses.clsP_cajacierre> items= new ArrayList<clsClasses.clsP_cajacierre>();

    public clsP_cajacierreObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_cajacierre item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_cajacierre item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_cajacierre item) {
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

    public clsClasses.clsP_cajacierre first() {
        return items.get(0);
    }

    public clsClasses.clsP_cajacierre last() {
        return items.get(count-1);
    }


    // Private

    private void addItem(clsClasses.clsP_cajacierre item) {

        ins.init("P_cajacierre");

        ins.add("SUCURSAL",item.sucursal);
        ins.add("RUTA",item.ruta);
        ins.add("COREL",item.corel);
        ins.add("ESTADO",item.estado);
        ins.add("FECHA",item.fecha);
        ins.add("VENDEDOR",item.vendedor);
        ins.add("CODPAGO",item.codpago);
        ins.add("FONDOCAJA",item.fondocaja);
        ins.add("MONTOINI",item.montoini);
        ins.add("MONTOFIN",item.montofin);
        ins.add("MONTODIF",item.montodif);
        ins.add("STATCOM",item.statcom);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_cajacierre item) {

        upd.init("P_cajacierre");

        upd.add("ESTADO",item.estado);
        upd.add("FECHA",item.fecha);
        upd.add("VENDEDOR",item.vendedor);
        upd.add("FONDOCAJA",item.fondocaja);
        upd.add("MONTOINI",item.montoini);
        upd.add("MONTOFIN",item.montofin);
        upd.add("MONTODIF",item.montodif);
        upd.add("STATCOM",item.statcom);

        upd.Where("(SUCURSAL='"+item.sucursal+"') AND (RUTA='"+item.ruta+"') AND (COREL="+item.corel+") AND (CODPAGO="+item.codpago+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_cajacierre item) {
        sql="DELETE FROM P_cajacierre WHERE (SUCURSAL='"+item.sucursal+"') AND (RUTA='"+item.ruta+"') AND (COREL="+item.corel+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM P_cajacierre WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_cajacierre item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_cajacierre();

            item.sucursal=dt.getString(0);
            item.ruta=dt.getString(1);
            item.corel=dt.getInt(2);
            item.estado=dt.getInt(3);
            item.fecha=dt.getLong(4);
            item.vendedor=dt.getString(5);
            item.codpago=dt.getInt(6);
            item.fondocaja=dt.getDouble(7);
            item.montoini=dt.getDouble(8);
            item.montofin=dt.getDouble(9);
            item.montodif=dt.getDouble(10);
            item.statcom=dt.getString(11);

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

    public String addItemSql(clsClasses.clsP_cajacierre item) {

        ins.init("P_cajacierre");

        ins.add("SUCURSAL",item.sucursal);
        ins.add("RUTA",item.ruta);
        ins.add("COREL",item.corel);
        ins.add("ESTADO",item.estado);
        ins.add("FECHA",item.fecha);
        ins.add("VENDEDOR",item.vendedor);
        ins.add("CODPAGO",item.codpago);
        ins.add("FONDOCAJA",item.fondocaja);
        ins.add("MONTOINI",item.montoini);
        ins.add("MONTOFIN",item.montofin);
        ins.add("MONTODIF",item.montodif);
        ins.add("STATCOM",item.statcom);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_cajacierre item) {

        upd.init("P_cajacierre");

        upd.add("ESTADO",item.estado);
        upd.add("FECHA",item.fecha);
        upd.add("VENDEDOR",item.vendedor);
        upd.add("FONDOCAJA",item.fondocaja);
        upd.add("MONTOINI",item.montoini);
        upd.add("MONTOFIN",item.montofin);
        upd.add("MONTODIF",item.montodif);
        upd.add("STATCOM",item.statcom);

        upd.Where("(SUCURSAL='"+item.sucursal+"') AND (RUTA='"+item.ruta+"') AND (COREL="+item.corel+") AND (CODPAGO="+item.codpago+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

