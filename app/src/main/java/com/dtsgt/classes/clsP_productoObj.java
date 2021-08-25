package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_productoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_producto";
    private String sql;
    public ArrayList<clsClasses.clsP_producto> items= new ArrayList<clsClasses.clsP_producto>();

    public clsP_productoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_producto item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_producto item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_producto item) {
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

    public clsClasses.clsP_producto first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_producto item) {

        ins.init("P_producto");

        ins.add("CODIGO",item.codigo);
        ins.add("TIPO",item.tipo);
        ins.add("LINEA",item.linea);
        ins.add("SUBLINEA",item.sublinea);
        ins.add("EMPRESA",item.empresa);
        ins.add("MARCA",item.marca);
        ins.add("CODBARRA",item.codbarra);
        ins.add("DESCCORTA",item.desccorta);
        ins.add("DESCLARGA",item.desclarga);
        ins.add("COSTO",item.costo);
        ins.add("FACTORCONV",item.factorconv);
        ins.add("UNIDBAS",item.unidbas);
        ins.add("UNIDMED",item.unidmed);
        ins.add("UNIMEDFACT",item.unimedfact);
        ins.add("UNIGRA",item.unigra);
        ins.add("UNIGRAFACT",item.unigrafact);
        ins.add("DESCUENTO",item.descuento);
        ins.add("BONIFICACION",item.bonificacion);
        ins.add("IMP1",item.imp1);
        ins.add("IMP2",item.imp2);
        ins.add("IMP3",item.imp3);
        ins.add("VENCOMP",item.vencomp);
        ins.add("DEVOL",item.devol);
        ins.add("OFRECER",item.ofrecer);
        ins.add("RENTAB",item.rentab);
        ins.add("DESCMAX",item.descmax);
        ins.add("PESO_PROMEDIO",item.peso_promedio);
        ins.add("MODIF_PRECIO",item.modif_precio);
        ins.add("IMAGEN",item.imagen);
        ins.add("VIDEO",item.video);
        ins.add("VENTA_POR_PESO",item.venta_por_peso);
        ins.add("ES_PROD_BARRA",item.es_prod_barra);
        ins.add("UNID_INV",item.unid_inv);
        ins.add("VENTA_POR_PAQUETE",item.venta_por_paquete);
        ins.add("VENTA_POR_FACTOR_CONV",item.venta_por_factor_conv);
        ins.add("ES_SERIALIZADO",item.es_serializado);
        ins.add("PARAM_CADUCIDAD",item.param_caducidad);
        ins.add("PRODUCTO_PADRE",item.producto_padre);
        ins.add("FACTOR_PADRE",item.factor_padre);
        ins.add("TIENE_INV",item.tiene_inv);
        ins.add("TIENE_VINETA_O_TUBO",item.tiene_vineta_o_tubo);
        ins.add("PRECIO_VINETA_O_TUBO",item.precio_vineta_o_tubo);
        ins.add("ES_VENDIBLE",item.es_vendible);
        ins.add("UNIGRASAP",item.unigrasap);
        ins.add("UM_SALIDA",item.um_salida);
        ins.add("ACTIVO",item.activo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_producto item) {

        upd.init("P_producto");

        upd.add("TIPO",item.tipo);
        upd.add("LINEA",item.linea);
        upd.add("SUBLINEA",item.sublinea);
        upd.add("EMPRESA",item.empresa);
        upd.add("MARCA",item.marca);
        upd.add("CODBARRA",item.codbarra);
        upd.add("DESCCORTA",item.desccorta);
        upd.add("DESCLARGA",item.desclarga);
        upd.add("COSTO",item.costo);
        upd.add("FACTORCONV",item.factorconv);
        upd.add("UNIDBAS",item.unidbas);
        upd.add("UNIDMED",item.unidmed);
        upd.add("UNIMEDFACT",item.unimedfact);
        upd.add("UNIGRA",item.unigra);
        upd.add("UNIGRAFACT",item.unigrafact);
        upd.add("DESCUENTO",item.descuento);
        upd.add("BONIFICACION",item.bonificacion);
        upd.add("IMP1",item.imp1);
        upd.add("IMP2",item.imp2);
        upd.add("IMP3",item.imp3);
        upd.add("VENCOMP",item.vencomp);
        upd.add("DEVOL",item.devol);
        upd.add("OFRECER",item.ofrecer);
        upd.add("RENTAB",item.rentab);
        upd.add("DESCMAX",item.descmax);
        upd.add("PESO_PROMEDIO",item.peso_promedio);
        upd.add("MODIF_PRECIO",item.modif_precio);
        upd.add("IMAGEN",item.imagen);
        upd.add("VIDEO",item.video);
        upd.add("VENTA_POR_PESO",item.venta_por_peso);
        upd.add("ES_PROD_BARRA",item.es_prod_barra);
        upd.add("UNID_INV",item.unid_inv);
        upd.add("VENTA_POR_PAQUETE",item.venta_por_paquete);
        upd.add("VENTA_POR_FACTOR_CONV",item.venta_por_factor_conv);
        upd.add("ES_SERIALIZADO",item.es_serializado);
        upd.add("PARAM_CADUCIDAD",item.param_caducidad);
        upd.add("PRODUCTO_PADRE",item.producto_padre);
        upd.add("FACTOR_PADRE",item.factor_padre);
        upd.add("TIENE_INV",item.tiene_inv);
        upd.add("TIENE_VINETA_O_TUBO",item.tiene_vineta_o_tubo);
        upd.add("PRECIO_VINETA_O_TUBO",item.precio_vineta_o_tubo);
        upd.add("ES_VENDIBLE",item.es_vendible);
        upd.add("UNIGRASAP",item.unigrasap);
        upd.add("UM_SALIDA",item.um_salida);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO='"+item.codigo+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_producto item) {
        sql="DELETE FROM P_producto WHERE (CODIGO='"+item.codigo+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM P_producto WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_producto item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_producto();

            item.codigo=dt.getString(0);
            item.tipo=dt.getString(1);
            item.linea=dt.getString(2);
            item.sublinea=dt.getString(3);
            item.empresa=dt.getString(4);
            item.marca=dt.getString(5);
            item.codbarra=dt.getString(6);
            item.desccorta=dt.getString(7);
            item.desclarga=dt.getString(8);
            item.costo=dt.getDouble(9);
            item.factorconv=dt.getDouble(10);
            item.unidbas=dt.getString(11);
            item.unidmed=dt.getString(12);
            item.unimedfact=dt.getDouble(13);
            item.unigra=dt.getString(14);
            item.unigrafact=dt.getDouble(15);
            item.descuento=dt.getString(16);
            item.bonificacion=dt.getString(17);
            item.imp1=dt.getDouble(18);
            item.imp2=dt.getDouble(19);
            item.imp3=dt.getDouble(20);
            item.vencomp=dt.getString(21);
            item.devol=dt.getString(22);
            item.ofrecer=dt.getString(23);
            item.rentab=dt.getString(24);
            item.descmax=dt.getString(25);
            item.peso_promedio=dt.getDouble(26);
            item.modif_precio=dt.getInt(27);
            item.imagen=dt.getString(28);
            item.video=dt.getString(29);
            item.venta_por_peso=dt.getInt(30);
            item.es_prod_barra=dt.getInt(31);
            item.unid_inv=dt.getString(32);
            item.venta_por_paquete=dt.getInt(33);
            item.venta_por_factor_conv=dt.getInt(34);
            item.es_serializado=dt.getInt(35);
            item.param_caducidad=dt.getInt(36);
            item.producto_padre=dt.getString(37);
            item.factor_padre=dt.getDouble(38);
            item.tiene_inv=dt.getInt(39);
            item.tiene_vineta_o_tubo=dt.getInt(40);
            item.precio_vineta_o_tubo=dt.getDouble(41);
            item.es_vendible=dt.getInt(42);
            item.unigrasap=dt.getDouble(43);
            item.um_salida=dt.getString(44);
            item.activo=dt.getInt(45);

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

    public String addItemSql(clsClasses.clsP_producto item) {

        ins.init("P_producto");

        ins.add("CODIGO",item.codigo);
        ins.add("TIPO",item.tipo);
        ins.add("LINEA",item.linea);
        ins.add("SUBLINEA",item.sublinea);
        ins.add("EMPRESA",item.empresa);
        ins.add("MARCA",item.marca);
        ins.add("CODBARRA",item.codbarra);
        ins.add("DESCCORTA",item.desccorta);
        ins.add("DESCLARGA",item.desclarga);
        ins.add("COSTO",item.costo);
        ins.add("FACTORCONV",item.factorconv);
        ins.add("UNIDBAS",item.unidbas);
        ins.add("UNIDMED",item.unidmed);
        ins.add("UNIMEDFACT",item.unimedfact);
        ins.add("UNIGRA",item.unigra);
        ins.add("UNIGRAFACT",item.unigrafact);
        ins.add("DESCUENTO",item.descuento);
        ins.add("BONIFICACION",item.bonificacion);
        ins.add("IMP1",item.imp1);
        ins.add("IMP2",item.imp2);
        ins.add("IMP3",item.imp3);
        ins.add("VENCOMP",item.vencomp);
        ins.add("DEVOL",item.devol);
        ins.add("OFRECER",item.ofrecer);
        ins.add("RENTAB",item.rentab);
        ins.add("DESCMAX",item.descmax);
        ins.add("PESO_PROMEDIO",item.peso_promedio);
        ins.add("MODIF_PRECIO",item.modif_precio);
        ins.add("IMAGEN",item.imagen);
        ins.add("VIDEO",item.video);
        ins.add("VENTA_POR_PESO",item.venta_por_peso);
        ins.add("ES_PROD_BARRA",item.es_prod_barra);
        ins.add("UNID_INV",item.unid_inv);
        ins.add("VENTA_POR_PAQUETE",item.venta_por_paquete);
        ins.add("VENTA_POR_FACTOR_CONV",item.venta_por_factor_conv);
        ins.add("ES_SERIALIZADO",item.es_serializado);
        ins.add("PARAM_CADUCIDAD",item.param_caducidad);
        ins.add("PRODUCTO_PADRE",item.producto_padre);
        ins.add("FACTOR_PADRE",item.factor_padre);
        ins.add("TIENE_INV",item.tiene_inv);
        ins.add("TIENE_VINETA_O_TUBO",item.tiene_vineta_o_tubo);
        ins.add("PRECIO_VINETA_O_TUBO",item.precio_vineta_o_tubo);
        ins.add("ES_VENDIBLE",item.es_vendible);
        ins.add("UNIGRASAP",item.unigrasap);
        ins.add("UM_SALIDA",item.um_salida);
        ins.add("ACTIVO",item.activo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_producto item) {

        upd.init("P_producto");

        upd.add("TIPO",item.tipo);
        upd.add("LINEA",item.linea);
        upd.add("SUBLINEA",item.sublinea);
        upd.add("EMPRESA",item.empresa);
        upd.add("MARCA",item.marca);
        upd.add("CODBARRA",item.codbarra);
        upd.add("DESCCORTA",item.desccorta);
        upd.add("DESCLARGA",item.desclarga);
        upd.add("COSTO",item.costo);
        upd.add("FACTORCONV",item.factorconv);
        upd.add("UNIDBAS",item.unidbas);
        upd.add("UNIDMED",item.unidmed);
        upd.add("UNIMEDFACT",item.unimedfact);
        upd.add("UNIGRA",item.unigra);
        upd.add("UNIGRAFACT",item.unigrafact);
        upd.add("DESCUENTO",item.descuento);
        upd.add("BONIFICACION",item.bonificacion);
        upd.add("IMP1",item.imp1);
        upd.add("IMP2",item.imp2);
        upd.add("IMP3",item.imp3);
        upd.add("VENCOMP",item.vencomp);
        upd.add("DEVOL",item.devol);
        upd.add("OFRECER",item.ofrecer);
        upd.add("RENTAB",item.rentab);
        upd.add("DESCMAX",item.descmax);
        upd.add("PESO_PROMEDIO",item.peso_promedio);
        upd.add("MODIF_PRECIO",item.modif_precio);
        upd.add("IMAGEN",item.imagen);
        upd.add("VIDEO",item.video);
        upd.add("VENTA_POR_PESO",item.venta_por_peso);
        upd.add("ES_PROD_BARRA",item.es_prod_barra);
        upd.add("UNID_INV",item.unid_inv);
        upd.add("VENTA_POR_PAQUETE",item.venta_por_paquete);
        upd.add("VENTA_POR_FACTOR_CONV",item.venta_por_factor_conv);
        upd.add("ES_SERIALIZADO",item.es_serializado);
        upd.add("PARAM_CADUCIDAD",item.param_caducidad);
        upd.add("PRODUCTO_PADRE",item.producto_padre);
        upd.add("FACTOR_PADRE",item.factor_padre);
        upd.add("TIENE_INV",item.tiene_inv);
        upd.add("TIENE_VINETA_O_TUBO",item.tiene_vineta_o_tubo);
        upd.add("PRECIO_VINETA_O_TUBO",item.precio_vineta_o_tubo);
        upd.add("ES_VENDIBLE",item.es_vendible);
        upd.add("UNIGRASAP",item.unigrasap);
        upd.add("UM_SALIDA",item.um_salida);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO='"+item.codigo+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

