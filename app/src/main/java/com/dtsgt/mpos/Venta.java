package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses;
import com.dtsgt.base.clsClasses.clsVenta;
import com.dtsgt.classes.SwipeListener;
import com.dtsgt.classes.clsBonFiltro;
import com.dtsgt.classes.clsBonif;
import com.dtsgt.classes.clsBonifGlob;
import com.dtsgt.classes.clsDeGlob;
import com.dtsgt.classes.clsDescFiltro;
import com.dtsgt.classes.clsDescuento;
import com.dtsgt.classes.clsKeybHandler;
import com.dtsgt.classes.clsP_lineaObj;
import com.dtsgt.ladapt.ListAdaptGridFam;
import com.dtsgt.ladapt.ListAdaptGridFamList;
import com.dtsgt.ladapt.ListAdaptGridProd;
import com.dtsgt.ladapt.ListAdaptGridProdList;
import com.dtsgt.ladapt.ListAdaptMenuVenta;
import com.dtsgt.ladapt.ListAdaptVenta;

import java.util.ArrayList;

public class Venta extends PBase {

    private ListView listView;
    private GridView gridView,grdbtn,grdfam,grdprod;
    private TextView lblTot,lblTit,lblAlm,lblVend,lblNivel,lblCant,lblBarra;
    private TextView lblProd,lblDesc,lblStot,lblKeyDP,lblPokl,lblDir;
    private EditText txtBarra,txtFilter;
    private ImageView imgroad,imgscan;
    private RelativeLayout relScan;

    private ArrayList<clsVenta> items= new ArrayList<clsVenta>();
    private ListAdaptVenta adapter;
    private clsVenta selitem;
    private Precio prc;
    private clsKeybHandler khand;

    private ListAdaptMenuVenta adaptergrid;
    private ListAdaptMenuVenta adapterb;
    private ListAdaptGridFam adapterf;
    private ListAdaptGridFamList adapterfl;
    private ListAdaptGridProd adapterp;
    private ListAdaptGridProdList adapterpl;

    private AlertDialog.Builder mMenuDlg;

    private ArrayList<clsClasses.clsMenu> mitems= new ArrayList<clsClasses.clsMenu>();
    private ArrayList<clsClasses.clsMenu> mmitems= new ArrayList<clsClasses.clsMenu>();
    private ArrayList<clsClasses.clsMenu> fitems= new ArrayList<clsClasses.clsMenu>();
    private ArrayList<clsClasses.clsMenu> pitems= new ArrayList<clsClasses.clsMenu>();
    private ArrayList<String> lcode = new ArrayList<String>();
    private ArrayList<String> lname = new ArrayList<String>();

    private int browse;

    private double cant,desc,mdesc,prec,precsin,imp,impval;
    private double descmon,tot,totsin,percep,ttimp,ttperc,ttsin,prodtot;
    private double px,py,cpx,cpy,cdist;

    private String emp,uid,seluid,cliid,prodid,uprodid,famid,um,tiposcan,barcode,imgfold,tipo;
    private int nivel,dweek,clidia,counter;
    private boolean sinimp,softscanexist,porpeso,usarscan,handlecant=true;
    private boolean decimal,menuitemadd,usarbio,imgflag,scanning=false;

    private AppMethods app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);

        super.InitBase();
        addlog("Venta",""+du.getActDateTime(),gl.vend);

        setControls();

        //gl.cliente="";
        //gl.iniciaVenta=false;
        gl.scancliente="";
        emp=gl.emp;
        nivel=1;
        gl.nivel=nivel;
        cliid=gl.cliente;cliid="0";
        decimal=false;

        gl.atentini=du.getActDateTime();
        gl.ateninistr=du.geActTimeStr();
        gl.climode=true;

        app = new AppMethods(this, gl, Con, db);
        app.parametrosExtra();

        counter=1;

        prc=new Precio(this,mu,2);
        khand=new clsKeybHandler(this,lblCant,lblKeyDP);

        menuItems();
        setHandlers();
        initValues();

        browse=0;
        txtBarra.requestFocus();txtBarra.setText("");
        clearItem();

        imgflag=gl.peMImg;
        setVisual();

        if(!gl.exitflag) {

            Handler mtimer = new Handler();
            Runnable mrunner=new Runnable() {
                @Override
                public void run() {
                    gl.scancliente="";
                    browse=8;

                    gl.iniciaVenta=false;

                    if (usarbio) {
                        startActivity(new Intent(Venta.this,Clientes.class));
                    } else {
                        if (!gl.cliposflag) {
                            gl.cliposflag=true;
                            if (!gl.exitflag) {
                                startActivity(new Intent(Venta.this,CliPos.class));
                            }
                        }
                    }
                }
            };
            mtimer.postDelayed(mrunner,100);
        }

    }

    //region Events

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try{
            //if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                barcode=contents;
                toast(barcode);
            }
            //}
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    public void doFocus(View view) {
        try {
            txtBarra.requestFocus();
        } catch (Exception e) {}
    }

    public void finishOrder(View view) {
        try{
            clsBonifGlob clsBonG;
            clsDeGlob clsDeG;
            String s,ss;

            if (!hasProducts())
            {
                mu.msgbox("No puede continuar, no ha vendido ninguno producto !");return;
            }

            if (gl.cliente.isEmpty()) {
                toast("Cliente pendiente");
                browse=8;
                startActivity(new Intent(this,Clientes.class));
                return;
            }

            gl.gstr="";
            browse=1;

            gl.bonprodid="*";
            gl.bonus.clear();

            clsDeG=new clsDeGlob(this,tot);ss="";

            if (clsDeG.tieneDesc()) {

                gl.descglob=clsDeG.valor;
                gl.descgtotal=clsDeG.vmonto;

                for (int i = 0; i <clsDeG.items.size(); i++) {
                    s=clsDeG.items.get(i).valor+" , "+clsDeG.items.get(i).lista;
                    ss=ss+s+"\n";
                }
            }

            ss=ss+"acum : "+clsDeG.acum+" , limit "+clsDeG.maxlimit+"\n";
            ss=ss+"Valor : "+clsDeG.valor+"\n";
            ss=ss+"acum : "+clsDeG.valacum+"\n";
            ss=ss+"max : "+clsDeG.valmax+"\n";
            //mu.msgbox(ss);

            // Bonificacion

            gl.bonprodid="*";
            gl.bonus.clear();

            clsBonG=new clsBonifGlob(this,tot);
            if (clsBonG.tieneBonif()) {
                for (int i = 0; i <clsBonG.items.size(); i++) {
                    //s=clsBonG.items.get(i).valor+"   "+clsBonG.items.get(i).tipolista+"  "+clsBonG.items.get(i).lista;
                    //Toast.makeText(this,s, Toast.LENGTH_SHORT).show();
                    gl.bonus.add(clsBonG.items.get(i));
                }
            } else {

            }

            if (gl.dvbrowse!=0){
                if (tot<gl.dvdispventa){
                    mu.msgbox("No puede totalizar la factura, es menor al monto permitido para la nota de crédito: " + gl.dvdispventa);return;
                }
            }
            gl.brw=0;

            browse=0;

            Intent intent = new Intent(this,FacturaRes.class);
            startActivity(intent);

            if (gl.bonus.size()>0) {
                //Intent intent = new Intent(this,BonList.class);
                //startActivity(intent);
            }
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox("finishOrder: "+e.getMessage());
        }
    }

    public void showPromo(View view){
        try{
            gl.gstr="*";
            browse=3;

            Intent intent = new Intent(this,ListaPromo.class);
            startActivity(intent);
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    public void doSoftScan(View view) {
		/*if (softscanexist) {
			try{
				browse=5;barcode="";

				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
				startActivityForResult(intent, 0);
			}catch (Exception e){
				addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			}
		//} else {*/
        doFocus(view);
        //}
    }

    public void doKey(View view) {
        khand.handleKey(view.getTag().toString());
        if (khand.isEnter) {
            barcode=khand.getStringValue();
            if (!barcode.isEmpty()) addBarcode();
            /*
            if (handlecant) {
                if (khand.isValid) {
                    gl.dval=khand.value;
                    processCant();
                }
            } else {
                barcode=khand.getStringValue();
                if (!barcode.isEmpty()) addBarcode();
            }
            */
         }
    }

    public void subItemClick(int position,int handle) {
    }

    public void doFocusScan(View view) {
        txtBarra.requestFocus();
    }

    private void setHandlers(){
        try{

            listView.setOnTouchListener(new SwipeListener(this) {
                public void onSwipeRight() {
                    onBackPressed();
                }
                public void onSwipeLeft() {
                    finishOrder(null);
                }
            });

            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        Object lvObj = listView.getItemAtPosition(position);
                        clsVenta item = (clsVenta)lvObj;

                        prodid=item.Cod;gl.prodmenu=prodid;
                        uprodid=prodid;
                        uid=item.emp;gl.menuitemid=uid;seluid=uid;
                        adapter.setSelectedIndex(position);

                        gl.gstr=item.Nombre;
                        gl.retcant=(int) item.Cant;
                        gl.limcant=getDisp(prodid);
                        menuitemadd=false;

                        tipo=prodTipo(prodid);
                        if (!tipo.equalsIgnoreCase("M")) {
                            browse=6;
                            startActivity(new Intent(Venta.this,VentaEdit.class));
                        } else {
                            gl.newmenuitem=false;
                            browse=7;
                            startActivity(new Intent(Venta.this,ProdMenu.class));
                        }

                    } catch (Exception e) {
                        addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                        mu.msgbox( e.getMessage());
                    }
                };
            });

            listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        Object lvObj = listView.getItemAtPosition(position);
                        clsVenta vItem = (clsVenta)lvObj;

                        prodid=vItem.Cod;
                        adapter.setSelectedIndex(position);

                        if (prodRepesaje(prodid) && gl.rutatipo.equalsIgnoreCase("V")) {
                            gl.gstr=prodid;
                            gl.gstr2=vItem.Nombre;
                            showItemMenu();
                        } else {
                            msgAskDel("Borrar producto");
                        }
                    } catch (Exception e) {
                        addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                        mu.msgbox( e.getMessage());
                    }
                    return true;
                }
            });
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }


        txtBarra.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,int count, int after) { }

            public void onTextChanged(CharSequence s, int start,int before, int count) {
                //mu.msgbox("start "+start+" before "+before+" count "+count);

                final CharSequence ss=s;

                if (!scanning) {
                    scanning=true;
                    Handler handlerTimer = new Handler();
                    handlerTimer.postDelayed(new Runnable(){
                        public void run() {
                            compareSC(ss);
                        }}, 500);
                }


            }
        });

        grdfam.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                try {
                    Object lvObj = grdfam.getItemAtPosition(position);
                    clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;
                    famid=item.Cod;

                    if (imgflag) {
                        adapterf.setSelectedIndex(position);
                    } else {
                        adapterfl.setSelectedIndex(position);
                    }

                    listProduct();
                } catch (Exception e) {
                    String ss=e.getMessage();
                }
            };
        });

        grdprod.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                try {
                    Object lvObj = grdprod.getItemAtPosition(position);
                    clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;

                    if (imgflag) {
                        adapterp.setSelectedIndex(position);
                    } else {
                        adapterpl.setSelectedIndex(position);
                    }

                    prodid=item.Cod;
                    gl.gstr=prodid;gl.prodmenu=prodid;
                    gl.pprodname=item.Name;
                    gl.um=app.umVenta(prodid);
                    menuitemadd=true;

                    processItem(false);

                } catch (Exception e) {
                    String ss=e.getMessage();
                }
            };
        });

        grdprod.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Object lvObj = grdprod.getItemAtPosition(position);
                    clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;

                    adapterp.setSelectedIndex(position);

                    prodid=item.Cod;
                    gl.gstr=prodid;gl.prodmenu=prodid;
                    gl.pprodname=item.Name;

                    msgAskAdd(item.Name);
                } catch (Exception e) {}
                return true;
            }
        });

        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                try {
                    Object lvObj = gridView.getItemAtPosition(position);
                    clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;

                    adaptergrid.setSelectedIndex(position);
                    processMenuMenu(item.ID);
                } catch (Exception e) {
                    String ss=e.getMessage();
                }
            };
        });

        grdbtn.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                try {
                    Object lvObj = grdbtn.getItemAtPosition(position);
                    clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;

                    adapterb.setSelectedIndex(position);
                    processMenuBtn(item.ID);
                } catch (Exception e) {
                    String ss=e.getMessage();
                }
            };
        });

    }

    //endregion

    //region Main

    public void listItems() {
        Cursor DT;
        clsVenta item;
        double tt,stot,tdesc,desc;
        int ii;

        items.clear();tot=0;ttimp=0;ttperc=0;tdesc=0;selidx=-1;ii=0;seluid="";

        try {
            sql="SELECT T_VENTA.PRODUCTO, P_PRODUCTO.DESCCORTA, T_VENTA.TOTAL, T_VENTA.CANT, T_VENTA.PRECIODOC, " +
                    "T_VENTA.DES, T_VENTA.IMP, T_VENTA.PERCEP, T_VENTA.UM, T_VENTA.PESO, T_VENTA.UMSTOCK, T_VENTA.DESMON, T_VENTA.EMPRESA  " +
                    "FROM T_VENTA INNER JOIN P_PRODUCTO ON P_PRODUCTO.CODIGO=T_VENTA.PRODUCTO "+
                    "ORDER BY P_PRODUCTO.DESCCORTA ";

            DT=Con.OpenDT(sql);

            if (DT.getCount()>0) {

                DT.moveToFirst();
                while (!DT.isAfterLast()) {

                    tt=DT.getDouble(2);

                    item = clsCls.new clsVenta();

                    item.Cod=DT.getString(0);
                    item.Nombre=DT.getString(1);
                    item.Cant=DT.getDouble(3);
                    item.Prec=DT.getDouble(4);
                    item.Desc=DT.getDouble(5);
                    item.sdesc=mu.frmdec(item.Prec);
                    item.imp=DT.getDouble(6);
                    item.percep=DT.getDouble(7);
                    if (prodPorPeso(item.Cod)) 	{
                        item.um=DT.getString(10);
                    } else {
                        //item.um=DT.getString(8);
                        item.um=app.umVenta(item.Cod);
                    }
                    item.Peso=DT.getDouble(9);
                    item.emp=DT.getString(12);
                    if (item.emp.equalsIgnoreCase(uid)) {
                        selidx=ii;
                        seluid=uid;
                    }
                    desc=DT.getDouble(11);tdesc+=desc;

                    item.val=mu.frmdecimal(item.Cant,gl.peDecImp)+" "+ltrim(item.um,6);
                    if (gl.usarpeso) {
                        item.valp=mu.frmdecimal(item.Peso,gl.peDecImp)+" "+ltrim(gl.umpeso,6);
                    } else {
                        item.valp=".";
                    }

                    if (sinimp) {
                        ttsin=tt-item.imp-item.percep;
                        item.Total=ttsin;
                    } else {
                        item.Total=tt;
                    }

                    items.add(item);

                    tot+=tt;
                    ttimp+=item.imp;
                    ttperc+=item.percep;

                    DT.moveToNext();ii++;
                }
            }

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox( e.getMessage());
        }

        adapter=new ListAdaptVenta(this,this, items);
        adapter.cursym=gl.peMon;
        listView.setAdapter(adapter);

        if (sinimp) {
            ttsin=tot-ttimp-ttperc;
            ttsin=mu.round(ttsin,2);
            lblTot.setText(gl.peMon+mu.frmcur(ttsin));
        } else {
            tot=mu.round(tot,2);
            tdesc=mu.round(tdesc,2);
            stot=tot-tdesc;
            lblTot.setText(gl.peMon+mu.frmcur(tot));
            lblDesc.setText("Desc : "+mu.frmcur(tdesc));
            lblStot.setText("Subt : "+mu.frmcur(stot));
        }

        if (selidx>-1) {
            adapter.setSelectedIndex(selidx);
            listView.smoothScrollToPosition(selidx);
        } else seluid="";

    }

    private void processItem(boolean updateitem){
        try{

            String pid=gl.gstr;
            if (mu.emptystr(pid)) return;

            prodid=pid;

            gl.um=app.umVenta(prodid); um=gl.um;
            gl.bonprodid=prodid;

            khand.enable();khand.focus();

            prodPrecio();

            gl.dval=1;
            gl.limcant=getDisp(prodid);
            tipo=prodTipo(prodid);

            if (!tipo.equalsIgnoreCase("M")) {
                if (gl.limcant!=0) {
                    processCant(updateitem);
                } else {
                    msgAskLimit("El producto no está disponible.\n¿Continuar con la venta?",updateitem);
                }
            } else {
                processMenuItem();
            }
       } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private boolean processProdBarra(String barra) {
        Cursor DT;

        txtBarra.setText("");
        txtBarra.requestFocus();

        try {
            sql="SELECT CODIGO,DESCLARGA FROM P_PRODUCTO WHERE (CODBARRA='"+barra+"') OR (CODIGO='"+barra+"')";
            DT=Con.OpenDT(sql);
            if (DT.getCount()==0) {
                toast("Barra no existe");return false;
            }

            DT.moveToFirst();
            gl.gstr=DT.getString(0);
            gl.pprodname=DT.getString(1);

            processItem(false);

            txtBarra.requestFocus();

            return true;
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox(e.getMessage());return false;
        }
    }

    private void setCant(){
        Cursor dt;
        double icant;

        try {
            sql="SELECT CANT FROM T_VENTA WHERE PRODUCTO='"+prodid+"'";
            dt=Con.OpenDT(sql);

            if(dt.getCount()>0) {
                dt.moveToFirst();
                icant = dt.getDouble(0);
            } else {
                icant =0;
            }
        } catch (Exception e) {
            icant=0;
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
        }

        khand.setValue(icant);
        khand.enable();khand.focus();
    }

    private void processCant(boolean updateitem){
        clsDescuento clsDesc;
        clsBonif clsBonif;
        Cursor DT;
        double cnt,vv;
        String s;

        cnt = gl.dval;
        if (cnt < 0) return;

        try {
            try {
                sql = "SELECT CODIGO,DESCCORTA FROM P_PRODUCTO WHERE CODIGO='" + prodid + "'";
                DT = Con.OpenDT(sql);
                DT.moveToFirst();
            } catch (Exception e) {
                addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
                mu.msgbox(e.getMessage());
            }

            cant = cnt;
            um = gl.um;

            // Bonificacion

            // Borra la anterior, si existe
            sql="DELETE FROM T_BONITEM WHERE Prodid='"+prodid+"'";
            db.execSQL(sql);

            desc = 0;
            prodPrecio();

            gl.bonprodcant = cant;
            gl.bonus.clear();

            vv = cant * prec;vv = mu.round(vv, 2);

            //region Descuento , Bonif

             /*
            clsBonif = new clsBonif(this, prodid, cant, vv);
            if (clsBonif.tieneBonif()) {
                for (int i = 0; i < clsBonif.items.size(); i++) {
                    gl.bonus.add(clsBonif.items.get(i));
                }
            }
            */

            clsDesc = new clsDescuento(this, prodid, cant);
            desc = clsDesc.getDesc();
            mdesc = clsDesc.monto;

            if (desc + mdesc > 0) {

                browse = 3;
                gl.promprod = prodid;
                gl.promcant = cant;

                if (desc > 0) {
                    gl.prommodo = 0;
                    gl.promdesc = desc;
                } else {
                    gl.prommodo = 1;
                    gl.promdesc = mdesc;
                }

                startActivity(new Intent(this, DescBon.class));

            } else {
                /*
                if (gl.bonus.size() > 0) {
                    Intent intent = new Intent(this, BonList.class);
                    startActivity(intent);
                }
                */
            }

            //prodPrecio();

            /*
            precsin = prc.precsin;
            imp = prc.imp;
            impval = prc.impval;
            descmon = prc.descmon;
            tot = prc.tot;
            prodtot = tot;
            totsin = prc.totsin;
            percep = 0; */

            //endregion

            tipo=prodTipo(prodid);

            if (!tipo.equalsIgnoreCase("M")) {
                if (updateitem) {
                    if (updateItemUID()) clearItem();
                } else {
                    if (addItem()) clearItem();
                }
            } else {
                if (updateitem) {
                    if (updateMenuItemUID()) clearItem();
                } else {
                    if (addItemMenu()) clearItem();
                 }
            }
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }

        try {
            sql="DELETE FROM T_VENTA WHERE CANT=0";
            db.execSQL(sql);
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }
    }

    private void processMenuItem() {

        gl.menuitemid=""+counter;
        gl.newmenuitem=true;
        gl.gstr=gl.pprodname;
        gl.retcant=1;

        browse=7;
        startActivity(new Intent(this,ProdMenu.class));

    }

    private void updateCant() {

        if (gl.retcant<0 || gl.limcant==0) return;

        prodid=uprodid;
        tipo=prodTipo(prodid);

        gl.dval=gl.retcant;
        gl.limcant=getDisp(prodid);
        processCant(true);
        listItems();

    }

    private void processCantMenu() {
        gl.dval=gl.retcant;
        if (menuitemadd) {
           processCant(false);
        } else {
           processCant(true);
        }
    }

    private void updDesc(){
        try{
            desc=gl.promdesc;
            prodPrecio();
            updItem();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void prodPrecio() {
        try{
            if (prodPorPeso(prodid)) {
                prec = prc.precio(prodid, cant, nivel, um, gl.umpeso, gl.dpeso,um);
                if (prc.existePrecioEspecial(prodid,cant,gl.cliente,gl.clitipo,um,gl.umpeso,gl.dpeso)) {
                    if (prc.precioespecial>0) prec=prc.precioespecial;
                }
            } else {
                prec = prc.precio(prodid, cant, nivel, um, gl.umpeso, 0,um);
                if (prc.existePrecioEspecial(prodid,cant,gl.cliente,gl.clitipo,um,gl.umpeso,0)) {
                    if (prc.precioespecial>0) prec=prc.precioespecial;
                }
            }

            prec=mu.round(prec,2);
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
        }
    }

    private boolean addItem(){
        Cursor dt;
        double precdoc,fact,cantbas,peso;
        String umb;

        tipo=prodTipo(prodid);

        if (!tipo.equalsIgnoreCase("M")) {
            try {
                sql="SELECT Empresa,Cant FROM T_VENTA WHERE (PRODUCTO='"+prodid+"')";
                dt=Con.OpenDT(sql);
                if (dt.getCount()>0) {
                    openItem();return true;
                }
            } catch (SQLException e) {
                mu.msgbox("Error : " + e.getMessage());return false;
            }
        }

        try {
            sql="SELECT UNIDADMINIMA,FACTORCONVERSION FROM P_FACTORCONV WHERE (PRODUCTO='"+prodid+"') AND (UNIDADSUPERIOR='"+gl.um+"')";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            umb=dt.getString(0);
            fact=dt.getDouble(1);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            umb=um;fact=1;
        }

        cantbas=cant*fact;
        peso=mu.round(gl.dpeso*gl.umfactor,gl.peDec);
        prodtot=mu.round(prec*cant,2);

        try {

            if (sinimp) precdoc=precsin; else precdoc=prec;

            ins.init("T_VENTA");

            ins.add("PRODUCTO",prodid);
            ins.add("EMPRESA",""+counter);
            if (porpeso) ins.add("UM",gl.umpeso);else ins.add("UM",gl.um);
            ins.add("CANT",cant);
            ins.add("UMSTOCK",gl.um);
            if (gl.umfactor==0) gl.umfactor=1;
            ins.add("FACTOR",gl.umfactor);
            if (porpeso) ins.add("PRECIO",gl.prectemp); else ins.add("PRECIO",prec);
            ins.add("IMP",impval);
            ins.add("DES",desc);
            ins.add("DESMON",descmon);
            ins.add("TOTAL",prodtot);
            if (porpeso) ins.add("PRECIODOC",gl.prectemp); else ins.add("PRECIODOC",precdoc);
            ins.add("PESO",peso);
            ins.add("VAL1",0);
            ins.add("VAL2","");
            ins.add("VAL3",0);
            ins.add("VAL4","");
            ins.add("PERCEP",percep);

            db.execSQL(ins.sql());

            counter++;
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());return false;
        }

        try {
            sql="DELETE FROM T_VENTA WHERE CANT=0";
            db.execSQL(sql);
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        listItems();

        return true;
    }

    private boolean addItemMenu(){
        Cursor dt;
        double precdoc,fact,cantbas,peso;
        String umb;


        try {
            sql="SELECT UNIDADMINIMA,FACTORCONVERSION FROM P_FACTORCONV WHERE (PRODUCTO='"+prodid+"') AND (UNIDADSUPERIOR='"+gl.um+"')";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            umb=dt.getString(0);
            fact=dt.getDouble(1);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            umb=um;fact=1;
        }

        cantbas=cant*fact;
        peso=mu.round(gl.dpeso*gl.umfactor,gl.peDec);
        prodtot=mu.round(prec*cant,2);

        try {

            if (sinimp) precdoc=precsin; else precdoc=prec;

            ins.init("T_VENTA");

            ins.add("PRODUCTO",prodid);
            ins.add("EMPRESA",""+counter);
            if (porpeso) ins.add("UM",gl.umpeso);else ins.add("UM",gl.um);
            ins.add("CANT",cant);
            ins.add("UMSTOCK",gl.um);
            if (gl.umfactor==0) gl.umfactor=1;
            ins.add("FACTOR",gl.umfactor);
            if (porpeso) ins.add("PRECIO",gl.prectemp); else ins.add("PRECIO",prec);
            ins.add("IMP",impval);
            ins.add("DES",desc);
            ins.add("DESMON",descmon);
            ins.add("TOTAL",prodtot);
            if (porpeso) ins.add("PRECIODOC",gl.prectemp); else ins.add("PRECIODOC",precdoc);
            ins.add("PESO",peso);
            ins.add("VAL1",0);
            ins.add("VAL2","");
            ins.add("VAL3",0);
            ins.add("VAL4","");
            ins.add("PERCEP",percep);

            db.execSQL(ins.sql());

            counter++;
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());return false;
        }

        try {
            sql="DELETE FROM T_VENTA WHERE CANT=0";
            db.execSQL(sql);
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        listItems();

        return true;
    }

    private void updItem(){

        try {

            upd.init("T_VENTA");

            upd.add("PRECIO",prec);
            upd.add("IMP",imp);
            upd.add("DES",desc);
            upd.add("DESMON",descmon);
            upd.add("TOTAL",tot);
            upd.add("PRECIODOC",prec);

            upd.Where("PRODUCTO='"+prodid+"'");

            db.execSQL(upd.sql());

        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        listItems();

    }

    private boolean updateItemUID(){
        double precdoc;

        try {

            prodtot=mu.round(prec*cant,2);
            if (sinimp) precdoc=precsin; else precdoc=prec;

            upd.init("T_VENTA");
            upd.add("CANT",cant);
            upd.add("PRECIO",prec);
            upd.add("IMP",imp);
            upd.add("IMP",imp);
            upd.add("DES",desc);
            upd.add("DESMON",descmon);
            upd.add("TOTAL",prodtot);
            upd.add("PRECIODOC",precdoc);

            upd.Where("EMPRESA='"+uid+"'");

            db.execSQL(upd.sql());

            listItems();

            return true;

        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());return false;
        }

    }

    private boolean updateMenuItemUID(){
        double precdoc;

        try {

            prodtot=mu.round(prec*cant,2);
            if (sinimp) precdoc=precsin; else precdoc=prec;

            upd.init("T_VENTA");
            upd.add("CANT",cant);
            upd.add("PRECIO",prec);
            upd.add("IMP",imp);
            upd.add("IMP",imp);
            upd.add("DES",desc);
            upd.add("DESMON",descmon);
            upd.add("TOTAL",prodtot);
            upd.add("PRECIODOC",precdoc);

            upd.Where("EMPRESA='"+uid+"'");

            db.execSQL(upd.sql());

            listItems();

            return true;

        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());return false;
        }

    }

    private void delItem(){
        try {
            db.execSQL("DELETE FROM T_VENTA WHERE PRODUCTO='"+prodid+"'");
            listItems();
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }
    }

    //endregion

    //region Barras

    private void addBarcode() {
        gl.barra=barcode;

        if (barraProducto()) {
            txtBarra.setText("");return;
        } else {
            toastlong("¡La barra "+barcode+" no existe!");
            txtBarra.setText("");
        }
    }

    private boolean barraBolsa() {
        Cursor dt;
        double ppeso=0,pprecdoc=0,factbolsa;
        String uum,umven;
        boolean isnew=true;

        porpeso=true;

        try {
            sql="SELECT CODIGO,CANT,PESO,UNIDADMEDIDA " +
                    "FROM P_STOCKB WHERE (BARRA='"+barcode+"') ";
            dt=Con.OpenDT(sql);
            if (dt.getCount()==0) return false;

            try {

                dt.moveToFirst();

                prodid = dt.getString(0);
                cant = dt.getInt(1);
                ppeso = dt.getDouble(2);
                uum = dt.getString(3);
                um=uum;
                umven=app.umVenta(prodid);
                factbolsa=app.factorPres(prodid,umven,um);
                cant=cant*factbolsa;

                //if (sinimp) precdoc=precsin; else precdoc=prec;

                if (prodPorPeso(prodid)) {
                    prec = prc.precio(prodid, cant, nivel, um, gl.umpeso, ppeso,umven);
                    if (prc.existePrecioEspecial(prodid,cant,gl.cliente,gl.clitipo,uum,gl.umpeso,ppeso)) {
                        if (prc.precioespecial>0) prec=prc.precioespecial;
                    }
                } else {
                    prec = prc.precio(prodid, cant, nivel, um, gl.umpeso, 0,umven);
                    if (prc.existePrecioEspecial(prodid,cant,gl.cliente,gl.clitipo,uum,gl.umpeso,0)) {
                        if (prc.precioespecial>0) prec=prc.precioespecial;
                    }
                }

                if (prodPorPeso(prodid)) prec=mu.round2(prec/ppeso);
                pprecdoc = prec;

                prodtot = prec;
                if (factbolsa>1) prodtot = cant*prec;
                if (prodPorPeso(prodid)) prodtot=mu.round2(prec*ppeso);

                try {
                    ins.init("T_BARRA");

                    ins.add("BARRA",barcode);
                    ins.add("CODIGO",prodid);
                    ins.add("PRECIO",prodtot);
                    ins.add("PESO",ppeso);
                    ins.add("PESOORIG",ppeso);
                    ins.add("CANTIDAD",cant);

                    db.execSQL(ins.sql());

                    toast(barcode);
                } catch (Exception e) {
                    isnew=false;
                    msgAskBarra("Borrar la barra "+barcode);return true;
                }

                prec=mu.round(prec,2);
                prodtot=mu.round(prodtot,2);

                ins.init("T_VENTA");

                ins.add("PRODUCTO",prodid);
                ins.add("EMPRESA",emp);
                if (prodPorPeso(prodid)) {
                    ins.add("UM",gl.umpeso);
                } else {
                    if (factbolsa==1) ins.add("UM",umven);else ins.add("UM",umven);
                }
                ins.add("CANT",cant);
                ins.add("UMSTOCK",umven);
                ins.add("FACTOR",gl.umfactor);
                if (prodPorPeso(prodid)) {
                    //ins.add("PRECIO",gl.prectemp);
                    ins.add("PRECIO",prec);
                } else {
                    ins.add("PRECIO",prec);
                }
                ins.add("IMP",0);
                ins.add("DES",0);
                ins.add("DESMON",0);
                ins.add("TOTAL",prodtot);
                if (prodPorPeso(prodid)) {
                    //ins.add("PRECIODOC",gl.prectemp);
                    ins.add("PRECIODOC",pprecdoc);
                } else {
                    ins.add("PRECIODOC",pprecdoc);
                }
                ins.add("PESO",ppeso);
                ins.add("VAL1",0);
                ins.add("VAL2","");
                ins.add("VAL3",0);
                ins.add("VAL4","");
                ins.add("PERCEP",percep);

                db.execSQL(ins.sql());

            } catch (SQLException e) {

            }

            actualizaTotalesBarra();

            if (isnew) validaBarraBon();

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return false;
    }

    private void actualizaTotalesBarra() {
        Cursor dt;
        int ccant;
        double ppeso,pprecio;

        try {

            //sql="SELECT COUNT(BARRA),SUM(PESO),SUM(PRECIO) FROM T_BARRA WHERE CODIGO='"+prodid+"'";
            //#CKFK 20190410 se modificó esta consulta para sumar la cantidad y no contar las barras
            sql="SELECT SUM(CANTIDAD),SUM(PESO),SUM(PRECIO) FROM T_BARRA WHERE CODIGO='"+prodid+"'";
            dt=Con.OpenDT(sql);

            ccant=0;ppeso=0;pprecio=0;

            if (dt.getCount()>0) {
                dt.moveToFirst();

                ccant=dt.getInt(0);
                ppeso=dt.getDouble(1);
                pprecio=dt.getDouble(2);
            }

            sql="UPDATE T_VENTA SET Cant="+ccant+",Peso="+ppeso+",Total="+pprecio+" WHERE PRODUCTO='"+prodid+"'";
            db.execSQL(sql);

            sql="DELETE FROM T_VENTA WHERE Cant=0";
            db.execSQL(sql);

            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean barraProducto() {
        Cursor dt;

        try {

            sql = "SELECT DISTINCT P_PRODUCTO.CODIGO, P_PRODUCTO.DESCCORTA " +
                    "FROM P_PRODUCTO INNER JOIN	P_STOCK ON P_STOCK.CODIGO=P_PRODUCTO.CODIGO INNER JOIN " +
                    "P_PRODPRECIO ON (P_STOCK.CODIGO=P_PRODPRECIO.CODIGO)  " +
                    "WHERE (P_STOCK.CANT > 0) AND ((P_PRODUCTO.CODBARRA='"+barcode+"') OR (P_PRODUCTO.CODIGO='"+barcode+"')) ";
            sql += "UNION ";
            sql += "SELECT DISTINCT P_PRODUCTO.CODIGO,P_PRODUCTO.DESCCORTA FROM P_PRODUCTO " +
                    "WHERE ((P_PRODUCTO.TIPO ='S') OR (P_PRODUCTO.TIPO ='M')) " +
                    "AND ((P_PRODUCTO.CODBARRA='"+barcode+"') OR (P_PRODUCTO.CODIGO='"+barcode+"'))  COLLATE NOCASE";

            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                khand.clear(true);

                gl.gstr=dt.getString(0);gl.um="UN";
                gl.pprodname=dt.getString(1);

                processItem(false);
                return true;
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return false;
    }

    private void borraBarra() {
        clsBonif clsBonif;
        int bcant,bontotal,boncant,bfaltcant,bon;
        String bprod="";

        try {
            db.execSQL("DELETE FROM T_BARRA WHERE BARRA='"+gl.barra+"' AND CODIGO='"+prodid+"'");
            actualizaTotalesBarra();

            gl.bonbarprod=prodid;

            bcant=cantBolsa();
            boncant=cantBonif();
            bfaltcant=cantFalt();

            clsBonif = new clsBonif(this, prodid, bcant, 0);
            if (clsBonif.tieneBonif()) {
                bon=(int) clsBonif.items.get(0).valor;
                bprod=clsBonif.items.get(0).lista;
                gl.bonbarid=clsBonif.items.get(0).lista;
            } else {
                bon=0;gl.bonbarid="";
            }

            bontotal=boncant+bfaltcant;

            //toast("Bolsas : "+bcant+" bon : "+bon+"  / "+bontotal);
            if (bon<bontotal) {
                removerBonif(bprod,(bontotal-bon));
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private boolean barraBonif() {
        Cursor dt;

        try {
            sql="SELECT PRODUCTO FROM T_BARRA_BONIF WHERE (BARRA='"+barcode+"')";
            dt=Con.OpenDT(sql);
            return dt.getCount()>0;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return true;
    }

    private void validaBarraBon() {
        clsBonif clsBonif;
        int bcant,bontotal,boncant,bfaltcant,bon;

        gl.bonbarprod=prodid;

        bcant=cantBolsa();
        boncant=cantBonif();
        bfaltcant=cantFalt();

        clsBonif = new clsBonif(this, prodid, bcant, 0);
        if (clsBonif.tieneBonif()) {
            bon=(int) clsBonif.items.get(0).valor;
            gl.bonbarid=clsBonif.items.get(0).lista;
        } else {
            bon=0;gl.bonbarid="";
        }

        bontotal=boncant+bfaltcant;

        //toast("Bolsas : "+bcant+" bon : "+bon+"  / "+bontotal);
        if (bon>bontotal) startActivity(new Intent(this,BonBarra.class));

    }

    private int cantBolsa() {
        try {
            sql="SELECT BARRA FROM T_BARRA WHERE CODIGO='"+prodid+"'";
            Cursor dt=Con.OpenDT(sql);
            return dt.getCount();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return 0;
        }
    }

    private int cantBonif() {
        try {
            sql="SELECT BARRA FROM T_BARRA_BONIF WHERE PRODUCTO='"+prodid+"'";
            Cursor dt=Con.OpenDT(sql);
            return dt.getCount();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            return 0;
        }
    }

    private int cantFalt() {
        try {
            sql="SELECT PRODID FROM T_BONIFFALT WHERE PRODUCTO='"+prodid+"'";
            Cursor dt=Con.OpenDT(sql);
            return dt.getCount();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            return 0;
        }
    }

    private void removerBonif(String bprod,int bcant) {
        Cursor dt;
        String barra,sbarra="";
        int bc=0;

        try {
            for (int i = 1; i == bcant; i++) {

                sql = "SELECT CANT FROM T_BONIFFALT WHERE (PRODID='"+prodid+"') ";
                dt = Con.OpenDT(sql);

                if (dt.getCount() > 0) {
                    dt.moveToFirst();

                    sql="UPDATE T_BONIFFALT SET CANT=CANT-1 WHERE (PRODID='"+prodid+"') ";
                    db.execSQL(sql);

                    sql="DELETE FROM T_BONIFFALT WHERE CANT=0";
                    db.execSQL(sql);

                } else {

                    sql = "SELECT BARRA FROM T_BARRA_BONIF WHERE (PRODUCTO='"+prodid+"') ";
                    dt = Con.OpenDT(sql);

                    if (dt.getCount() > 0) {
                        dt.moveToLast();
                        barra=dt.getString(0);sbarra+=barra+"\n";bc++;

                        sql = "DELETE FROM T_BARRA_BONIF WHERE (PRODUCTO='"+prodid+"') AND (BARRA='"+barra+"') ";
                        db.execSQL(sql);
                    }
                }
            }

            reportBonif();

            if (bc>0) msgbox("Las barra devueltas : \n"+sbarra);

        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }
    }

    private void reportBonif() {
        int bont,bon,bonf;

        bon=cantBonif();
        bonf=cantFalt();
        bont=bon+bonf;

        if (bonf==0) {
            toast("Bonificación actual : "+bon);
        } else {
            toast("Bonificación actual : "+bon+" / "+bont);
        }

    }

    //endregion

    //region No atencion

    private void listAten(){
        Cursor DT;
        String code,name;

        lcode.clear();lname.clear();

        try {

            sql="SELECT Codigo,Nombre FROM P_CODATEN ORDER BY Nombre";

            DT=Con.OpenDT(sql);
            if (DT.getCount()==0) {return;}

            DT.moveToFirst();
            while (!DT.isAfterLast()) {

                try {
                    code=String.valueOf(DT.getInt(0));
                    name=DT.getString(1);

                    lcode.add(code);
                    lname.add(name);
                } catch (Exception e) {
                    mu.msgbox(e.getMessage());
                }
                DT.moveToNext();
            }
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox( e.getMessage());return;
        }

        showAtenDialog();

    }

    public void showAtenDialog() {
        try{
            final AlertDialog Dialog;

            final String[] selitems = new String[lname.size()];
            for (int i = 0; i < lname.size(); i++) {
                selitems[i] = lname.get(i);
            }

            mMenuDlg = new AlertDialog.Builder(this);
            mMenuDlg.setTitle("Razón de no atencion");

            mMenuDlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    try {
                        String s=lcode.get(item);
                        setNoAtt(s);
                        doExit();
                    } catch (Exception e) {
                    }
                }
            });

            mMenuDlg.setNegativeButton("Regresar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            Dialog = mMenuDlg.create();
            Dialog.show();

            Button nbutton = Dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setBackgroundColor(Color.parseColor("#1A8AC6"));
            nbutton.setTextColor(Color.WHITE);
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void setNoAtt(String scna){
        int cna;

        try {
            cna=Integer.parseInt(scna);
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            return;
        }

        String cliid=gl.cliente;

        try
        {
            upd.init("P_CLIRUTA");
            upd.add("BANDERA",cna);
            upd.Where("CLIENTE='"+cliid+"' AND DIA="+dweek);

            db.execSQL(upd.sql());
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        saveAtten(""+cna);
    }

    private void saveAtten(String codnoate) {
        long ti,tf,td;

        ti=gl.atentini;tf=du.getActDateTime();
        td=du.timeDiff(tf,ti);if (td<1) td=1;

        try {
            ins.init("D_ATENCION");

            ins.add("RUTA",gl.ruta);
            ins.add("FECHA",ti);
            ins.add("HORALLEG",du.shora(ti)+":00");
            ins.add("HORASAL",du.shora(tf)+":00");
            ins.add("TIEMPO",td);

            ins.add("VENDEDOR",gl.vend);
            ins.add("CLIENTE",gl.cliente);
            ins.add("DIAACT",du.dayofweek(ti));
            ins.add("DIA",du.dayofweek(ti));
            ins.add("DIAFLAG","S");

            ins.add("SECUENCIA",1);
            ins.add("SECUENACT",1);
            ins.add("CODATEN",codnoate);
            ins.add("KILOMET",0);

            ins.add("VALORVENTA",0);
            ins.add("VALORNEXT",0);
            ins.add("CLIPORDIA",clidia);
            ins.add("CODOPER","X");
            ins.add("COREL","");

            if (gl.gpspass) ins.add("SCANNED","G");else ins.add("SCANNED",gl.escaneo);
            ins.add("STATCOM","N");
            ins.add("LLEGO_COMPETENCIA_ANTES",0);

            ins.add("CoorX",gl.gpspx);
            ins.add("CoorY",gl.gpspy);
            ins.add("CliCoorX",gl.gpscpx);
            ins.add("CliCoorY",gl.gpscpy);
            ins.add("Dist",gl.gpscdist);

            db.execSQL(ins.sql());
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            //mu.msgbox("Error : " + e.getMessage());
        }

    }

    //endregion

    //region Dialogs

    private void msgAskExit(String msg) {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle(R.string.app_name);
            dialog.setMessage(msg  + " ?");
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    gl.cliposflag=true;
                    if (gl.dvbrowse!=0) gl.dvbrowse =0;
                    browse=0;
                    gl.forcedclose=true;
                    doExit();
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void msgAskDel(String msg) {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle(R.string.app_name);
            dialog.setMessage(msg  + " ?");
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    delItem();
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { }
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void msgAskBarra(String msg) {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle(R.string.app_name);
            dialog.setMessage(msg  + " ?");
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    borraBarra();
                 }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { }
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }


    }

    private void msgAskAdd(String msg) {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Agregar a la venta");
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }


    }

    private void msgAskLimit(String msg,boolean updateitem) {
        final boolean updatem=updateitem;
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle(R.string.app_name);
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    processCant(updatem);
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }


    //endregion

    //region Menu

    private void listFamily() {
        clsP_lineaObj P_lineaObj=new clsP_lineaObj(this,Con,db);
        clsClasses.clsMenu item;

        try {
            fitems.clear();
            P_lineaObj.fill("WHERE Activo=1");

            for (int i = 0; i <P_lineaObj.count; i++) {
                item=clsCls.new clsMenu();
                item.Cod=P_lineaObj.items.get(i).codigo;
                item.Name=P_lineaObj.items.get(i).nombre;
                fitems.add(item);
            }

            if (imgflag) {
                adapterf=new ListAdaptGridFam(this,fitems,imgfold);
                grdfam.setAdapter(adapterf);
            } else {
                adapterfl=new ListAdaptGridFamList(this,fitems,imgfold);
                grdfam.setAdapter(adapterfl);
            }

        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }

    }

    private void listProduct() {
        Cursor dt;
        clsClasses.clsMenu item;
        ArrayList<String> pcodes = new ArrayList<String>();
        String pcode;

        try {
            pitems.clear();pcodes.clear();

            sql = "SELECT DISTINCT P_PRODUCTO.CODIGO, P_PRODUCTO.DESCCORTA, P_PRODPRECIO.UNIDADMEDIDA,P_PRODUCTO.ACTIVO " +
                    "FROM P_PRODUCTO INNER JOIN	P_STOCK ON P_STOCK.CODIGO=P_PRODUCTO.CODIGO INNER JOIN " +
                    "P_PRODPRECIO ON (P_STOCK.CODIGO=P_PRODPRECIO.CODIGO)  " +
                    "WHERE (P_STOCK.CANT > 0) AND (P_PRODUCTO.ACTIVO=1)";
            if (!mu.emptystr(famid)) {
                if (!famid.equalsIgnoreCase("0"))  sql = sql + "AND (P_PRODUCTO.LINEA='" + famid + "') ";
            }

            sql += "UNION ";
            sql += "SELECT DISTINCT P_PRODUCTO.CODIGO,P_PRODUCTO.DESCCORTA,'',P_PRODUCTO.ACTIVO  " +
                    "FROM P_PRODUCTO " +
                    "WHERE ((P_PRODUCTO.TIPO ='S') OR (P_PRODUCTO.TIPO ='M'))";
            if (!mu.emptystr(famid)) {
                if (!famid.equalsIgnoreCase("0"))
                    sql = sql + "AND (P_PRODUCTO.LINEA='" + famid + "') ";
            }

            sql += "ORDER BY P_PRODUCTO.DESCCORTA";
            dt=Con.OpenDT(sql);

            if (dt.getCount()==0){
                msgbox("¡Ninguno articulo de la familia tiene existencia disponible!");return;
            }

            dt.moveToFirst();

            while (!dt.isAfterLast()) {

                pcode=dt.getString(0);
                if (!pcodes.contains(pcode)) {
                    if (dt.getInt(3)==1) {
                        item=clsCls.new clsMenu();
                        item.Cod=dt.getString(0);
                        item.Name=dt.getString(1)+" \n[ "+gl.peMon+prodPrecioBase(item.Cod)+" ]";
                        pitems.add(item);pcodes.add(pcode);
                    }
                }

                dt.moveToNext();
            }
        } catch (Exception e) 		{
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
        }

        if (imgflag) {
            adapterp=new ListAdaptGridProd(this,pitems,imgfold);
            grdprod.setAdapter(adapterp);
        } else {
            adapterpl=new ListAdaptGridProdList(this,pitems,imgfold);
            grdprod.setAdapter(adapterpl);
        }

    }

    private void menuItems() {
        clsClasses.clsMenu item;

        try {
            mitems.clear();

            try {

                item = clsCls.new clsMenu();
                item.ID=3;item.Name="Reimpresión";item.Icon=3;
                mitems.add(item);

                if (gl.rol>1) {
                    item = clsCls.new clsMenu();
                    item.ID=4;item.Name="Anulación";item.Icon=4;
                    mitems.add(item);
                }

                item = clsCls.new clsMenu();
                item.ID=14;item.Name="Actualizar";item.Icon=14;
                mitems.add(item);

                /*
                item = clsCls.new clsMenu();
                item.ID=7;item.Name="Existencias";item.Icon=7;
                mitems.add(item);

                item = clsCls.new clsMenu();
                item.ID=101;item.Name="Baktún";item.Icon=101;
                mitems.add(item);
                */

            } catch (Exception e) {
                addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            }

            adaptergrid=new ListAdaptMenuVenta(this, mitems);
            gridView.setAdapter(adaptergrid);
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

        try {
            mmitems.clear();

            try {

                item = clsCls.new clsMenu();
                item.ID=50;item.Name="Buscar ";item.Icon=50;
                mmitems.add(item);

                item = clsCls.new clsMenu();
                item.ID=52;item.Name="Cliente";item.Icon=52;
                mmitems.add(item);

                item = clsCls.new clsMenu();
                item.ID=54;item.Name="Borrar linea ";item.Icon=54;
                mmitems.add(item);

                item = clsCls.new clsMenu();
                item.ID=55;item.Name="Borrar todo ";item.Icon=55;
                mmitems.add(item);

                //item = clsCls.new clsMenu();
                //item.ID=51;item.Name="Barra";item.Icon=51;
                //mmitems.add(item);

                //item = clsCls.new clsMenu();
                //item.ID=53;item.Name="Bloquear";item.Icon=53;
                //mmitems.add(item);

            } catch (Exception e) {
                addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            }

            adapterb=new ListAdaptMenuVenta(this, mmitems);
            grdbtn.setAdapter(adapterb);
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void processMenuMenu(int menuid) {
        try {
            switch (menuid) {
                case 3:
                    menuImprDoc(3);break;
                case 4:
                    gl.tipo=3;menuAnulDoc();break;
                case 14:
                    showQuickRecep();break;
            }
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }

    public void showPrintMenuTodo() {

        try {
            final AlertDialog Dialog;
            //final String[] selitems = {"Factura","Pedido","Recibo","Deposito","Recarga","Devolución a bodega","Cierre de dia", "Nota crédito"};
            final String[] selitems = {(gl.peMFact?"Factura":"Ticket"),"Deposito","Recarga","Devolución a bodega"};

            AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
            menudlg.setIcon(R.drawable.reimpresion48);
            menudlg.setTitle("Reimpresión");

            menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    switch (item) {
                        case 0:
                            menuImprDoc(3);break;
                        case 1:
                            menuImprDoc(2);break;
                        case 2:
                            menuImprDoc(4);break;
                        case 3:
                            menuImprDoc(5);break;
                    }

                    dialog.cancel();
                }
            });

            menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            Dialog = menudlg.create();
            Dialog.show();

            Button nbutton = Dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setBackgroundColor(Color.parseColor("#1A8AC6"));
            nbutton.setTextColor(Color.WHITE);
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    public void menuImprDoc(int doctipo) {
        try{
            gl.tipo=doctipo;

            Intent intent = new Intent(this,Reimpresion.class);
            startActivity(intent);
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    public void showVoidMenuTodo() {
        try{
            final AlertDialog Dialog;
            final String[] selitems = {(gl.peMFact?"Factura":"Ticket"),"Deposito","Recarga","Devolución a bodega"};


            AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
            menudlg.setIcon(R.drawable.anulacion48);
            menudlg.setTitle("Anulación");

            menudlg.setItems(selitems ,	new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    switch (item) {
                        case 0:
                            gl.tipo=3;break;
                        case 1:
                            gl.tipo=2;break;
                        case 2:
                            gl.tipo=4;break;
                        case 3:
                            gl.tipo=5;break;
                    }

                    menuAnulDoc();
                    dialog.cancel();
                }
            });

            menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            Dialog = menudlg.create();
            Dialog.show();

            Button nbutton = Dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setBackgroundColor(Color.parseColor("#1A8AC6"));
            nbutton.setTextColor(Color.WHITE);
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    public void showQuickRecep() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Actualización de parametros");
        dialog.setMessage("¿Actualizar parametros de venta?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    gl.findiaactivo=false;
                    gl.tipo = 0;
                    gl.autocom = 0;
                    gl.modoadmin = false;
                    gl.comquickrec = true;
                    startActivity(new Intent(Venta.this, ComWS.class));
                } catch (Exception e) {
                    msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                }
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void menuAnulDoc() {
        try{
            Intent intent = new Intent(this,Anulacion.class);
            startActivity(intent);
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void processMenuBtn(int menuid) {
        try {

            switch (menuid) {
                case 50:
                    gl.gstr = "";browse = 1;gl.prodtipo = 1;
                    startActivity(new Intent(this, Producto.class));break;
                case 51:
                    if (khand.isValid) {
                        barcode=khand.val;addBarcode();
                    }
                    break;
                case 52:
                    if (!gl.exitflag) {
                        browse=8;
                        gl.climode=false;

                        if (usarbio) {
                            startActivity(new Intent(Venta.this,Clientes.class));
                        } else {
                            if (!gl.forcedclose) {
                                startActivity(new Intent(Venta.this,CliPos.class));
                            }
                        }
                    }
                    break;
                case 53:
                    break;
                case 54:
                    borraLinea();break;
                case 55:
                    borraTodo();
                    break;
            }
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }

    private void borraLinea() {
        if (seluid.isEmpty()) return;

        try {
            sql="DELETE  FROM T_VENTA WHERE EMPRESA='"+seluid+"'";
            db.execSQL(sql);
            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void borraTodo() {
        msgAskTodo("Borrar toda la venta");
    }

    private void msgAskTodo(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Venta");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    sql="DELETE FROM T_VENTA";
                    db.execSQL(sql);
                    listItems();
                } catch (Exception e) {
                    msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                }
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    //endregion

    //region Aux

    private void showItemMenu() {
        try{
            final AlertDialog Dialog;
            final String[] selitems = {"Repesaje","Borrar"};

            AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
            menudlg.setTitle("Producto venta");

            menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch (item) {
                        case 0:
                            browse=4;
                            startActivity(new Intent(Venta.this,RepesajeLista.class));break;
                        case 1:
                            msgAskDel("Borrar producto");break;
                    }

                    dialog.cancel();
                }
            });

            menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            Dialog = menudlg.create();
            Dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void setControls(){

        try{
            listView = (ListView) findViewById(R.id.listView1);
            gridView = (GridView) findViewById(R.id.gridView2);
            grdfam = (GridView) findViewById(R.id.grdFam);
            grdprod = (GridView) findViewById(R.id.grdProd);
            grdbtn = (GridView) findViewById(R.id.grdbtn);

            lblTot= (TextView) findViewById(R.id.lblTot);
            lblDesc= (TextView) findViewById(R.id.textView115);lblDesc.setText( "Desc : "+mu.frmcur(0));
            lblStot= (TextView) findViewById(R.id.textView103); lblStot.setText("Subt : "+mu.frmcur(0));
            lblTit= (TextView) findViewById(R.id.lblTit);
            lblAlm= (TextView) findViewById(R.id.lblTit2);
            lblVend= (TextView) findViewById(R.id.lblTit4);
            lblNivel= (TextView) findViewById(R.id.lblTit3);
            lblPokl= (TextView) findViewById(R.id.lblTit5);

            lblCant= (TextView) findViewById(R.id.lblCant);lblCant.setText("");
            lblBarra= (TextView) findViewById(R.id.textView122);lblBarra.setText("");
            lblKeyDP=(TextView) findViewById(R.id.textView110);
            lblDir=(TextView) findViewById(R.id.lblDir);

            imgroad= (ImageView) findViewById(R.id.imgRoadTit);
            imgscan= (ImageView) findViewById(R.id.imageView13);

            txtBarra=(EditText) findViewById(R.id.editText10);

            relScan= (RelativeLayout) findViewById(R.id.relScan);

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void setVisual() {
        if (imgflag) {
            grdfam.setNumColumns(3);
            grdprod.setNumColumns(3);
        } else {
            grdfam.setNumColumns(2);
            grdprod.setNumColumns(1);
        }

        listFamily();
    }

    private void initValues(){
        Cursor DT;
        String contrib;

        app.parametrosExtra();
        usarbio=gl.peMMod.equalsIgnoreCase("1");

        tiposcan="*";

        lblTit.setText(gl.cajanom);
        lblAlm.setText(gl.tiendanom);
        lblPokl.setText(gl.vendnom);

        try {
            sql="SELECT TIPO_HH FROM P_ARCHIVOCONF WHERE RUTA='"+gl.ruta+"'";
            DT=Con.OpenDT(sql);
            DT.moveToFirst();

            tiposcan=DT.getString(0);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            tiposcan="*";
        }

        usarscan=false;softscanexist=false;
        if (!mu.emptystr(tiposcan)) {
            if (tiposcan.equalsIgnoreCase("SOFTWARE")) {
                softscanexist=detectBarcodeScanner();
                usarscan=true;
            }
            if (!tiposcan.equalsIgnoreCase("SIN ESCANER")) usarscan=true;
        }

        if (usarscan) {
            imgscan.setVisibility(View.VISIBLE);
        } else {
            imgscan.setVisibility(View.INVISIBLE);
        }

        try {
            sql="SELECT INITPATH FROM P_EMPRESA WHERE EMPRESA='"+emp+"'";
            DT=Con.OpenDT(sql);
            DT.moveToFirst();

            String sim=DT.getString(0);
            sinimp=sim.equalsIgnoreCase("S");

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            sinimp=false;
        }

		/*
		contrib=gl.contrib;
		if (contrib.equalsIgnoreCase("C")) sinimp=true;
		if (contrib.equalsIgnoreCase("F")) sinimp=false;
		*/
        sinimp=false;
        gl.sinimp=sinimp;

        try {
            sql="DELETE FROM T_VENTA";
            db.execSQL(sql);

            sql="DELETE FROM T_BARRA";
            db.execSQL(sql);

            sql="DELETE FROM T_BARRA_BONIF";
            db.execSQL(sql);

            sql="DELETE FROM T_BONIFFALT";
            db.execSQL(sql);

            sql="DELETE FROM T_PRODMENU";
            db.execSQL(sql);

        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        try {
            sql="DELETE FROM T_PAGO";
            db.execSQL(sql);
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        try {
            sql="DELETE FROM T_BONIFFALT";
            db.execSQL(sql);
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        try {
            sql="DELETE FROM T_BONITEM";
            db.execSQL(sql);
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        gl.ref1="";
        gl.ref2="";
        gl.ref3="";

        clsDescFiltro clsDFilt=new clsDescFiltro(this,gl.ruta,gl.cliente);

        clsBonFiltro clsBFilt=new clsBonFiltro(this,gl.ruta,gl.cliente);

        imgfold= Environment.getExternalStorageDirectory()+ "/RoadFotos/";

        dweek=mu.dayofweek();

        lblTot.setText("Total : "+gl.peMon+mu.frmdec(0));
        lblVend.setText("");

        khand.clear(true);khand.enable();
    }

    private boolean hasProducts(){
        Cursor DT;

        try {
            sql="SELECT PRODUCTO FROM T_VENTA";
            DT=Con.OpenDT(sql);

            return DT.getCount()>0;
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            return false;
        }
    }

    private void showCredit(){
        try{
            if (hasCredits()){
                Intent intent = new Intent(this,Cobro.class);
                startActivity(intent);
            }
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private boolean hasCredits(){
        Cursor DT;

        try {
            sql="SELECT SALDO FROM P_COBRO WHERE CLIENTE='"+cliid+"'";
            DT=Con.OpenDT(sql);
            if (DT.getCount()>0) return true;
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox(e.getMessage());
        }

        return false;
    }

    private void doExit(){
        try{
            gl.exitflag=true;
            gl.cliposflag=false;
            super.finish();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    public String ltrim(String ss,int sw) {
        try{
            int l=ss.length();
            if (l>sw) {
                ss=ss.substring(0,sw);
            } else {
                String frmstr="%-"+sw+"s";
                ss=String.format(frmstr,ss);
            }
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
        return ss;
    }

    private String prodTipo(String prodid) {
        try {
            return app.prodTipo(prodid);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            return "P";
        }
    }

    private boolean prodPorPeso(String prodid) {
        try {
            return app.ventaPeso(prodid);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            return false;
        }
    }

    private boolean prodBarra(String prodid) {
        try {
            return app.prodBarra(prodid);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            return false;
        }
    }

    private boolean prodRepesaje(String prodid) {
        try {
            return app.ventaRepesaje(prodid);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            return false;
        }
    }

    private boolean detectBarcodeScanner() {

        String packagename="com.google.zxing.client.android";
        PackageManager pm = this.getPackageManager();

        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            toast("Aplicacion ZXing Barcode Scanner no esta instalada");return false;
        }

    }

    private String prodPrecioBase(String prid) {
        Cursor DT;
        double pr,stot,pprec,tsimp;
        String sprec="";

        try {

            sql="SELECT PRECIO FROM P_PRODPRECIO WHERE (CODIGO='"+prid+"') AND (NIVEL="+nivel+") ";
            DT=Con.OpenDT(sql);
            DT.moveToFirst();

            pr=DT.getDouble(0);

        } catch (Exception e) {
            pr=0;
        }

        sprec=mu.frmdec(pr);

        return sprec;

    }

    private int getDisp(String prid) {
        Cursor DT;
        String tipo;

        try {
            sql = "SELECT TIPO FROM P_PRODUCTO WHERE CODIGO='" + prid + "'";
            DT = Con.OpenDT(sql);
            DT.moveToFirst();
            tipo=DT.getString(0);

            if (tipo.equalsIgnoreCase("S")) return -1;
            if (tipo.equalsIgnoreCase("M")) return -1;

            sql = " SELECT SUM(CANT) FROM P_STOCK S WHERE CODIGO='"+prid+"'";
            DT=Con.OpenDT(sql);
            if (DT.getCount()==0) return 0;else DT.moveToFirst();

            return DT.getInt(0);
        } catch (Exception e) {
            //toast(e.getMessage());
            return 0;
        }
    }

    private void clearItem() {
        prodid="";gl.pprodname="";cant=0;prec=0;
    }

    private void openItem() {

        try {
            for (int i = 0; i <items.size(); i++) {

                if (items.get(i).Cod.equalsIgnoreCase(prodid)) {

                    clsVenta item = items.get(i);

                    prodid=item.Cod;gl.prodmenu=prodid;
                    uprodid=prodid;
                    uid=item.emp;
                    gl.gstr=item.Nombre;
                    gl.retcant=(int) item.Cant;
                    gl.limcant=getDisp(prodid);
                    browse=6;

                    startActivity(new Intent(Venta.this,VentaEdit.class));
                 }
            }

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }
    }

    private void cargaCliente() {
        Cursor DT;
        String ss;
        double lcred,cred,disp;

        browse=0;

        gl.exitflag=false;
        if (!gl.scancliente.isEmpty()) gl.cliente=gl.scancliente;
        gl.scancliente="";
        if (gl.cliente.isEmpty()) {
            toast("Cliente pendiente");return;
        }

        try {
            sql = "SELECT NOMBRE,LIMITECREDITO,NIT,DIRECCION,MEDIAPAGO FROM P_CLIENTE WHERE CODIGO='"+gl.cliente+"'";
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            gl.fnombre=DT.getString(0);
            gl.fnit=DT.getString(2);
            gl.fdir=DT.getString(3);

            gl.media=DT.getInt(4);

            lcred=DT.getDouble(1);
            if (lcred>0) {
                cred=totalCredito();
                if (cred>=0) {
                    disp=lcred-cred;if (disp<0) disp=0;
                } else disp=0;
             } else {
                disp=0;
            }

            gl.credito=disp;
            if (disp>0) ss=" [Cred: "+mu.frmcur(disp)+" ]";else ss="";

            lblVend.setText(DT.getString(0)+" "+ss);
        } catch (Exception e) {
            lblVend.setText("");
        }
    }

    private double totalCredito() {
        Cursor dt;
        double cred=0;
        int mm,yy;
        long ff;

        try {
            mm=du.getmonth(du.getActDate());
            yy=du.getyear(du.getActDate());
            ff=du.cfecha(yy,mm,1);

            sql="SELECT SUM(D_FACTURAP.VALOR) FROM D_FACTURAP  "+
               "INNER JOIN D_FACTURA ON D_FACTURAP.COREL=D_FACTURA.COREL "+
               "WHERE (D_FACTURA.FECHA>="+ff+") AND (D_FACTURA.ANULADO='N') " +
               "AND (D_FACTURA.CLIENTE='"+gl.cliente+"') AND (D_FACTURAP.TIPO='C')";
            dt = Con.OpenDT(sql);

            try {
                cred=dt.getDouble(0);
            } catch (Exception e) {
                cred=0;
            }

        } catch (Exception e) {
            msgbox("No se puede determinar crédito dispónible, error : "+e.getMessage());return -1;
        }

        return cred;
    }

    private void compareSC(CharSequence s) {
        String os,bc;

        bc=txtBarra.getText().toString();
        if (bc.isEmpty() || bc.length()<2) {
            txtBarra.setText("");
            scanning=false;
            return;
        }
        os=s.toString();

        if (bc.equalsIgnoreCase(os)) {
            barcode=bc;
            addBarcode();
        }

        txtBarra.setText("");txtBarra.requestFocus();
        scanning=false;
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        try{
            super.onResume();

            if (gl.forcedclose) {
                super.finish();
                return;
            }

            gl.climode=true;

            try {
                txtBarra.requestFocus();
            } catch (Exception e) {
            }

            if (gl.iniciaVenta) {

                browse=0;
                lblVend.setText(" ");

                try  {
                    db.execSQL("DELETE FROM T_VENTA");
                    listItems();
                } catch (SQLException e){
                    addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
                    mu.msgbox("Error : " + e.getMessage());
                }

                Handler mtimer = new Handler();
                Runnable mrunner=new Runnable() {
                    @Override
                    public void run() {
                        browse=8;
                        gl.iniciaVenta=false;

                        if (usarbio) {
                            startActivity(new Intent(Venta.this,Clientes.class));
                        } else {
                            if (!gl.cliposflag) {
                                gl.cliposflag=true;
                                if (!gl.forcedclose) {
                                    startActivity(new Intent(Venta.this,CliPos.class));
                                }
                            }
                        }
                    }
                };
                mtimer.postDelayed(mrunner,100);
            } else {
            }

            if (!gl.scancliente.isEmpty()) cargaCliente();

            if (browse==-1)   {
                browse=0;finish();return;
            }

            if (browse==1)   {
                browse=0;processItem(false);return;
            }

            if (browse==2) {
                browse=0;processCant(false);return;
            }

            if (browse==3) {
                browse=0;if (gl.promapl) updDesc();return;
            }

            if (browse==4) {
                browse=0;listItems();return;
            }

            if (browse==5) {
                browse=0;addBarcode();return;
            }

            if (browse==6) {
                browse=0;updateCant();return;
            }

            if (browse==7) {
                browse=0;processCantMenu();return;
            }

            if (browse==8) {
                browse=0;
                if (gl.forcedclose) {
                    super.finish();
                } else {
                    cargaCliente();
                }
            }

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    @Override
    public void onBackPressed() {
        try{
            msgAskExit("Regresar al menu principal");
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

}
