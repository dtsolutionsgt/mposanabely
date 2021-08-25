package com.dtsgt.base;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.dtsgt.mpos.R;

public class BaseDatosVersion {

	public ArrayList<String> items=new ArrayList<String>();
	
	private Context cont;
	private SQLiteDatabase db;
	private BaseDatos Con;
	private String sql;
    private Cursor dt;
	
	private int aver,pver;
	private boolean flag;
	
	public BaseDatosVersion(Context context,SQLiteDatabase dbase,BaseDatos dbCon) {
		cont=context;
		db=dbase;
		Con=dbCon;
	}

    public void update(){
        if (!update01()) return;
    }


	private boolean update01() {

		try {

			db.beginTransaction();

            sql="CREATE TABLE [P_usgrupo] ("+
                    "CODIGO TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "CUENTA TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE TABLE [P_usgrupoopc] ("+
                    "GRUPO TEXT NOT NULL,"+
                    "OPCION INTEGER NOT NULL,"+
                    "PRIMARY KEY ([GRUPO],[OPCION])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE TABLE [P_usopcion] ("+
                    "CODIGO INTEGER NOT NULL,"+
                    "MENUGROUP TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            db.execSQL(sql);

			db.setTransactionSuccessful();
			db.endTransaction();
		
		} catch (Exception e) {
			db.endTransaction();
		}

        flag=false;
        try {
            sql="SELECT * FROM P_usgrupo";
            dt=Con.OpenDT(sql);
            flag=dt.getCount()==0;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }

        if (!flag) return true;

        try {
            db.beginTransaction();

            db.execSQL("DELETE FROM P_usgrupo");
            db.execSQL("DELETE FROM P_usgrupoopc");
            db.execSQL("DELETE FROM P_usopcion");

            db.execSQL("INSERT INTO P_usgrupo VALUES (1,'Cajero','S')");
            db.execSQL("INSERT INTO P_usgrupo VALUES (2,'Supervisor','S')");
            db.execSQL("INSERT INTO P_usgrupo VALUES (3,'Gerente','S')");
            //db.execSQL("INSERT INTO P_usgrupo VALUES (4,'Mesero','S')");
            //db.execSQL("INSERT INTO P_usgrupo VALUES (5,'Vendedor','S')");

            db.execSQL("INSERT INTO P_usopcion VALUES ( 1,'Venta','Ingreso')");
            db.execSQL("INSERT INTO P_usopcion VALUES ( 2,'Caja','Ingreso')");
            db.execSQL("INSERT INTO P_usopcion VALUES ( 3,'Reimpresion','Ingreso')");
            db.execSQL("INSERT INTO P_usopcion VALUES ( 4,'Inventario','Ingreso')");
            db.execSQL("INSERT INTO P_usopcion VALUES ( 5,'Comunicacion','Ingreso')");
            db.execSQL("INSERT INTO P_usopcion VALUES ( 6,'Utilerias','Ingreso')");
            db.execSQL("INSERT INTO P_usopcion VALUES ( 7,'Mantenimientos','Ingreso')");
            db.execSQL("INSERT INTO P_usopcion VALUES ( 8,'Reportes','Ingreso')");
            db.execSQL("INSERT INTO P_usopcion VALUES ( 9,'Anulacion','Ingreso')");
            db.execSQL("INSERT INTO P_usopcion VALUES (10,'Mantenimientos','Botón agregar')");
            db.execSQL("INSERT INTO P_usopcion VALUES (11,'Mantenimientos','completos')");
            db.execSQL("INSERT INTO P_usopcion VALUES (12,'Mantenimientos','Clientes,Productos')");
            db.execSQL("INSERT INTO P_usopcion VALUES (13,'Mantenimientos','Botón guardar')");

            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('1',1)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('1',2)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('1',3)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('1',4)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('1',5)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('1',7)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('1',12)");

            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',1)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',2)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',3)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',4)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',5)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',6)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',7)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',8)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',9)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',10)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',11)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',12)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',13)");

            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',1)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',2)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',3)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',4)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',5)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',6)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',7)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',8)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',9)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',10)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',11)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',12)");
            db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',13)");

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage()+"\n"+sql);return false;
        }

        return true;
		
	}
	

	//region Aux
	
 	private void msgbox(String msg) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(cont);
    	
		dialog.setTitle(R.string.app_name);
		dialog.setMessage(msg);
					
		dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which) {  }
    	});
		dialog.show();
	
	}   		

	//endregion

}
