package com.dtsgt.base;

import android.app.Application;

import com.dtsgt.base.clsClasses.clsBonifItem;
import com.dtsgt.base.clsClasses.clsDemoDlg;
import com.epson.eposdevice.Device;
import com.epson.eposdevice.printer.Printer;

import java.util.ArrayList;

public class appGlobals extends Application {

	public String ruta,rutanom,sucur,rutatipo,rutatipog,vend,vendnom,gstr,gstr2,prod,prodmenu,um,umpres,umstock,cliente,clitipo;
	public String ubas,emp, empnom,imgpath,umpeso,lotedf,impresora, tipoImpresora, codSupervisor, ayudante, ayudanteID, vehiculo, vehiculoID;
	public String wsURL,bonprodid,bonbarid,bonbarprod,pprodname,contrib,ateninistr,tcorel,CodDev;
	public int itemid,gint,tipo,nivel,rol,prodtipo,prw,boldep,vnivel,vnivprec,media;
	public int autocom,pagomodo,filtrocli,prdlgmode,mantid,retcant,limcant,reportid,cajaid;
	public long nuevaFecha,atentini,lastDate;
	public double dval,dpeso,pagoval,pagolim,bonprodcant,percepcion,costo,credito,umfactor,prectemp,fondoCaja,FinMonto;
	public boolean CellCom,closeDevBod,modoinicial,newmenuitem,validDate,comquickrec;
	public String ref1,ref2,ref3,fnombre,fnit,fdir,escaneo,corel_d_mov,barra,parVer,gcods,prtipo,prpar;
	public String tienda,tiendanom,caja,cajanom,urlglob,menuitemid,titReport,pickcode,pickname;
	public int tiponcredito,validarCred,gpsdist,gcodi,savemantid;
    public boolean vcredito,vcheque,vchequepost,validimp,dev,banco,disc,iniciaVenta,listaedit,exitflag;
	public boolean closeCliDet,closeVenta,promapl,pagado,pagocobro,sinimp,rutapos,devol,modoadmin,reportList;
	public boolean usarpeso,banderafindia,depparc,incNoLectura,cobroPendiente,findiaactivo;
	public boolean banderaCobro,cliposflag, forcedclose,grantaccess;
	public int mpago,corelZ;

	//#CKFK 20190319 Para facilidades de desarrollo se debe colocar la variable debug en true por defecto estará en false
	public boolean debug=true;

	//Devolución Cliente
	public String devtipo,devrazon,dvumventa,dvumstock,dvumpeso,dvlote,scancliente;
	public double dvfactor,dvpeso,dvprec,dvpreclista,dvtotal;
	public int dvbrowse=0,tienelote,facturaVen,brw=0;
    public boolean dvporpeso,devfindia,devprncierre,gpspass,climode;
    public double dvdispventa;
    public String dvcorreld,dvcorrelnc,dvestado,dvactuald,dvactualnc;

	// Parametros Extra
	public String peModal,peMon,peFormatoFactura,peMMod,peFEL;
	public Boolean peStockItf,peSolicInv,peAceptarCarga,peBotInv,peBotPrec,endPrint;
	public Boolean peBotStock,peVehAyud,peEnvioParcial,peOrdPorNombre;
	public boolean peImprFactCorrecta;
	public int peDec,peDecCant,peDecImp,peLimiteGPS,peMargenGPS,peVentaGps;
    public boolean peMCent,peMPrOrdCos,peMImg,peMFact;

	// Descuentos
	public String promprod;
	public double promcant,promdesc,prommdesc,descglob,descgtotal;
	public int prommodo;
	
	// Bonificaciones
	
	public ArrayList<clsBonifItem> bonus = new ArrayList<clsBonifItem>();
	
	public clsDemoDlg clsDemo;
	
	// GPS
	public double gpspx,gpspy,gpscpx,gpscpy,gpscdist;
	
	//Id de Dispositivo Móvil
	public String deviceId,devicename;

	// Epson
	
	public Device mDevice=null;
	public Printer mPrinter=null;
	public boolean mPrinterSet=false;
	public String mPrinterIP;

	//Cobros
	public int escbro=0;

	//Desglose de efectivo
	public double totDep;

	//Comunicación con WS
	public int isOnWifi = 0;

}
