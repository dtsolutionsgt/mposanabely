package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_rutaObj;
import com.dtsgt.classes.clsP_sucursalObj;

import java.util.ArrayList;

public class ConfigCaja extends PBase {

    private Spinner spin,spin2;

    private ArrayList<String> spincode,spinlist,cajalist,cajacode;

    private String idsuc,idcaja;
    private int precpos=0;
    private boolean newitem=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_caja);

        super.InitBase();

        spin = (Spinner) findViewById(R.id.spinner10);
        spin2 = (Spinner) findViewById(R.id.spinner12);

        spincode=new ArrayList<String>();spinlist=new ArrayList<String>();
        cajacode=new ArrayList<String>();cajalist=new ArrayList<String>();

        setHandlers();

        loadItem();
    }

    //region Events

    public void doSave(View view) {
        if (validaDatos()) msgAskAdd("Aplicar asignacion");
    }

    public void doExit(View view) {
        msgAskExit("Salir");
    }

    private void setHandlers() {

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    TextView spinlabel = (TextView) parentView.getChildAt(0);
                    spinlabel.setTextColor(Color.BLACK);spinlabel.setPadding(5, 0, 0, 0);
                    spinlabel.setTextSize(21);spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                    idsuc = spincode.get(position);
                 } catch (Exception e) {
                    addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                    mu.msgbox(e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });

        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    TextView spinlabel = (TextView) parentView.getChildAt(0);
                    spinlabel.setTextColor(Color.BLACK);spinlabel.setPadding(5, 0, 0, 0);
                    spinlabel.setTextSize(21);spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                    idcaja = cajacode.get(position);
                } catch (Exception e) {
                    addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                    mu.msgbox(e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });

    }

    //endregion

    //region Main

    private void loadItem() {
        Cursor DT;

        try {
            sql = "SELECT SUCURSAL,RUTA FROM Params";
            DT = Con.OpenDT(sql);

            idsuc=DT.getString(0);
            idcaja=DT.getString(1);

            fillSpinner(idsuc);
            fillSpinner2(idcaja);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void updateItem() {
        try {
            sql = "UPDATE Params SET SUCURSAL='"+idsuc+"',RUTA='"+idcaja+"'";
            db.execSQL(sql);

            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private boolean validaDatos() {
        try {
            if (idsuc.isEmpty()) {
                msgbox("Falta definir tienda");return false;
            }

            if (idcaja.isEmpty()) {
                msgbox("Falta definir caja");return false;
            }

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private boolean fillSpinner(String selid){
        clsP_sucursalObj sucur =new clsP_sucursalObj(this,Con,db);
        int selidx=0;
        String scod;

        spincode.clear();spinlist.clear();
        spincode.add("");spinlist.add("");

        try {
            sucur.fill(" WHERE (Activo=1) OR (Codigo='"+selid+"') ORDER BY Nombre");
            if (sucur.count==0) {
                msgAskReturn("Lista de sucursales está vacia, no se puede continuar");return false;
            }

            for (int i = 0; i <sucur.count; i++) {
                scod=sucur.items.get(i).codigo;
                spincode.add(scod);
                spinlist.add(sucur.items.get(i).descripcion);
                if (scod.equalsIgnoreCase(selid)) selidx=i+1;
            }
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinlist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin.setAdapter(dataAdapter);

        try {
            spin.setSelection(selidx);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

        return true;

    }

    private boolean fillSpinner2(String selid){
        clsP_rutaObj ruta =new clsP_rutaObj(this,Con,db);
        int selidx=0;
        String scod;

        cajacode.clear();cajalist.clear();
        cajacode.add("");cajalist.add("");

        try {
            ruta.fill(" WHERE (Activo='S') OR (Codigo='"+selid+"') ORDER BY Nombre");
            if (ruta.count==0) {
                msgAskReturn("Lista de rutas está vacia, no se puede continuar");return false;
            }

            for (int i = 0; i <ruta.count; i++) {
                scod=ruta.items.get(i).codigo;
                cajacode.add(scod);
                cajalist.add(ruta.items.get(i).nombre);
                if (scod.equalsIgnoreCase(selid)) selidx=i+1;
            }
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, cajalist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin2.setAdapter(dataAdapter);

        try {
            spin2.setSelection(selidx);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

        return true;

    }

    //endregion

    //region Dialogs

    private void msgAskAdd(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                updateItem();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }



    private void msgAskReturn(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Familias");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.show();
    }

    private void msgAskExit(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    //endregion

    //region Activity Events

    @Override
    public void onBackPressed() {
        msgAskExit("Salir");
    }

    //endregion
}
