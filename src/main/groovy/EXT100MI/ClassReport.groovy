/*
 ***************************************************************
 *                                                             *
 *                           NOTICE                            *
 *                                                             *
 *   THIS SOFTWARE IS THE PROPERTY OF AND CONTAINS             *
 *   CONFIDENTIAL INFORMATION OF INFOR AND/OR ITS AFFILIATES   *
 *   OR SUBSIDIARIES AND SHALL NOT BE DISCLOSED WITHOUT PRIOR  *
 *   WRITTEN PERMISSION. LICENSED CUSTOMERS MAY COPY AND       *
 *   ADAPT THIS SOFTWARE FOR THEIR OWN USE IN ACCORDANCE WITH  *
 *   THE TERMS OF THEIR SOFTWARE LICENSE AGREEMENT.            *
 *   ALL OTHER RIGHTS RESERVED.                                *
 *                                                             *
 *   (c) COPYRIGHT 2020 INFOR.  ALL RIGHTS RESERVED.           *
 *   THE WORD AND DESIGN MARKS SET FORTH HEREIN ARE            *
 *   TRADEMARKS AND/OR REGISTERED TRADEMARKS OF INFOR          *
 *   AND/OR ITS AFFILIATES AND SUBSIDIARIES. ALL RIGHTS        *
 *   RESERVED.  ALL OTHER TRADEMARKS LISTED HEREIN ARE         *
 *   THE PROPERTY OF THEIR RESPECTIVE OWNERS.                  *
 *                                                             *
 ***************************************************************
 */

/*
 *Modification area - M3
 *Nbr               Date      User id     Description
 *ABF_R_0100        20220405  RDRIESSEN   Mods BF0100- Write to extension file EXTCLX as a basis for Receival and Classification Docket
 *ABF_R_0101        20220706  RDRIESSEN   Mods BF0100- exclude pusl = '99' , exclude write of detail lines in report 
 *ABF_R_0102        20220708  RDRIESSEN   Mods BF0100- run report to cater for multiple items per PO       
 
 *
 */
 

 import groovy.lang.Closure
 import java.time.LocalDate;
 import java.time.LocalDateTime;
 import java.time.format.DateTimeFormatter;
 import groovy.json.JsonSlurper;
 import java.math.BigDecimal;
 import java.math.RoundingMode;
 import java.text.DecimalFormat;


public class ClassReport extends ExtendM3Transaction {
  private final MIAPI mi;
  private final DatabaseAPI database;
  private final MICallerAPI miCaller;
  private final LoggerAPI logger;
  private final ProgramAPI program;
  private final IonAPI ion;
  
  //Input fields
  private String cono;
  private String divi;
  private String sudo;
  private String whlo;
  private String bano;
  private String itno;
  private String puno;
  private String pusl;
  private String pnli;
  private String grad;
  private String suno;
  private String grwe;
  private String tawe;
  private String sunm;
  private String loam;
  private String carm;
  private String newe;
  private String loa1;
  private String car1;
  private String adj01;
  private String adj02;
  private String adj03;
  private String adj04;
  private String adj05;
  private String adj06;
  private String adj07;
  private String adj08;
  private String adj09;
  private String adj10;
  private String adj11;
  private String adj12;
  private String adj13;
  private String adj14;
  private String adj15;
  private String adj16;
  private String adj17;
  private String adj18;
  private String adj19;
  private String adj20;
  private String adj21;
  private String adj22;
  private String adj23;
  private String adj24;
  private String rgdt;  
  private String adj01x;
  private String adj02x;
  private String adj03x;
  private String adj04x;
  private String adj05x;
  private String adj06x;
  private String adj07x;
  private String adj08x;
  private String adj09x;
  private String adj10x;
  private String adj11x;
  private String adj12x;
  private String adj13x;
  private String adj14x;
  private String adj15x;
  private String adj16x;
  private String adj17x;
  private String adj18x;
  private String adj19x;
  private String adj20x;
  private String adj21x;
  private String adj22x;
  private String adj23x;
  private String adj24x;
  private String ad1r;
  
  private String bref;
  private String cua1;
  private String cua2;
  private String re1a;
  private String re1b;
  private String re2a;
  private String re2b;
  private String re3a;
  private String re3b;
  private String ca1a;
  private String ca1b;
  private String lo1a;
  private String lo1b;
  private int XXCONO;
  private boolean found;
 
  public ClassReport(MIAPI mi, DatabaseAPI database, MICallerAPI miCaller, LoggerAPI logger, ProgramAPI program, IonAPI ion) {
    this.mi = mi;
    this.database = database;
  	this.miCaller = miCaller;
  	this.logger = logger;
  	this.program = program;
	  this.ion = ion;
	}
  
  public void main() {
    
    cono = mi.inData.get("CONO") == null ? '' : mi.inData.get("CONO").trim();
  	if (cono == "?") {
  	  cono = "";
  	} 
  	
  	divi = mi.inData.get("DIVI") == null ? '' : mi.inData.get("DIVI").trim();
  	if (divi == "?") {
  	  divi = "";
  	} 
  
  	whlo = mi.inData.get("WHLO") == null ? '' : mi.inData.get("WHLO").trim();
  	if (whlo == "?") {
  	  whlo = "";
  	} 
  	
  	suno = mi.inData.get("SUNO") == null ? '' : mi.inData.get("SUNO").trim();
  	if (suno == "?") {
  	  suno = "";
  	} 
  	
  	sudo = mi.inData.get("SUDO") == null ? '' : mi.inData.get("SUDO").trim();
  	if (sudo == "?") {
  	  sudo = "";
  	} 
  	
  
  	//Validate input fields
    if (!cono.isEmpty()) {
			if (cono.isInteger()){
				XXCONO= cono.toInteger();
			} else {
				mi.error("Company " + cono + " is invalid");
				return;
			}
		} else {
			XXCONO= program.LDAZD.CONO;
		}
		if (divi.isEmpty()) {
		  program.LDAZD.DIVI;
		} else {
		  DBAction queryCMNDIV = database.table("CMNDIV").index("00").selection("CCDIVI").build();
      DBContainer CMNDIV = queryCMNDIV.getContainer();
      CMNDIV.set("CCCONO", XXCONO);
      CMNDIV.set("CCDIVI", divi);
      if(!queryCMNDIV.read(CMNDIV)) {
        mi.error("Division does not exist.");
        return;
      } 
		}
  	// - validate whlo
    DBAction queryMITWHL = database.table("MITWHL").index("00").selection("MWWHLO").build();
    DBContainer MITWHL = queryMITWHL.getContainer();
    MITWHL.set("MWCONO", XXCONO);
    MITWHL.set("MWWHLO", whlo);
    if (!queryMITWHL.read(MITWHL)) {
      mi.error("Warehouse is invalid.");
      return;
    }
  	
  	// - validate suno
    DBAction queryCIDMAS = database.table("CIDMAS").index("00").selection("IDSUNO").build();
    DBContainer CIDMAS = queryCIDMAS.getContainer();
    CIDMAS.set("IDCONO", XXCONO);
    CIDMAS.set("IDSUNO", suno);
    if (!queryCIDMAS.read(CIDMAS)) {
      mi.error("Supplier is invalid.");
      return;
    }
  	
  	 // - validate sudo
  	found = false;
    DBAction queryFGRECL = database.table("FGRECL").index("30").selection("F2DIVI", "F2SUDO").build();
    DBContainer FGRECL = queryFGRECL.getContainer();
    FGRECL.set("F2CONO", XXCONO);
    FGRECL.set("F2DIVI", divi);
    FGRECL.set("F2SUDO", sudo);
    
    queryFGRECL.readAll(FGRECL, 3, 1, lstFGRECL);
    if (!found) {
      mi.error("Delivery No is invalid.");
      return;
    }
  	
    	// delete report workfile entries, subsequently recreate.
    deleteEXTCLX(cono, divi, whlo, suno, sudo);
    writeEXTCLX(cono, divi, whlo, suno, sudo);
    
  }
  /*
   * lstFGRECL - Callback function to return FGRECL records
   *
  */
  Closure<?> lstFGRECL = { DBContainer FGRECL ->
    found = true;
  }
  /*
   * delete record EXTCLX if exists for selected whlo-suno-sudo 
   *
  */
  def deleteEXTCLX(String cono, String divi, String whlo, String suno, String sudo) {
  
    DBAction queryEXTCLX = database.table("EXTCLX").index("00").selection("EXCONO", "EXDIVI", "EXWHLO", "EXSUNO", "EXSUDO").build();
    DBContainer EXTCLX = queryEXTCLX.getContainer();
    EXTCLX.set("EXCONO", XXCONO);
    EXTCLX.set("EXWHLO", whlo);
    EXTCLX.set("EXSUNO", suno);
    EXTCLX.set("EXSUDO", sudo);
    queryEXTCLX.readAllLock(EXTCLX, 4, deleteEXTCLX);
  }
  
  /*
  * deleteEXTCLX - Callback function
  *
  */
  Closure<?> deleteEXTCLX = { LockedResult EXTCLX ->
    EXTCLX.delete();
  }
  
  /*
  * Write EXTCLX record
  *
  */
  def writeEXTCLX(String cono, String divi, String whlo, String suno, String sudo) {
	  
    int currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger();
  	int currentTime = Integer.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")));
  	
    ExpressionFactory expression = database.getExpressionFactory("MITTRA");
    expression = expression.eq("MTSUDO", sudo);
    DBAction query = database.table("MITTRA").index("90").matching(expression).selection("MTCONO", "MTSUDO", "MTRGDT", "MTBANO", "MTITNO", "MTRIDN", "MTRIDL", "MTBREF").build();
    DBContainer container = query.getContainer();
    container.set("MTCONO", XXCONO);
    container.set("MTWHLO", whlo);
  
    query.readAll(container, 2, releasedItemProcessor);

  }
  
  /*
  * releasedItemProcessor - - Callback function to return MITTRA records
  *
  */
  Closure<?> releasedItemProcessor = { DBContainer container ->
    whlo = container.get("MTWHLO");
    bano = container.get("MTBANO");
    itno = container.get("MTITNO");
    puno = container.get("MTRIDN");
    pnli = container.get("MTRIDL");
    rgdt = container.get("MTRGDT");
    bref = container.get("MTBREF");
   
    def params01 = ["ITNO":itno.toString(), "BANO": bano.toString() ]; // toString is needed to convert from gstring to string
    
    String atnr = null;
    def callback01 = {
      Map<String, String> response ->
      if(response.ATNR != null){
        atnr = response.ATNR;  
      }
    }
  
	  miCaller.call("MMS235MI","GetItmLot", params01, callback01);	
	  
	  
    getAttributes(atnr);
   
  }
  
  /*
  * get attributes for the selected lotnumber retrieved from the previous MI call MMS235MI*
  *
  */
  def getAttributes(String atnr) {
     
    grwe = 0;
    newe = 0;
     
    def params02 = ["ATNR":atnr.toString()]; // toString is needed to convert from gstring to string
    
    def callback02 = {
      Map<String, String> response ->
     
      if(response.ATID.trim().equals("DRF01") && response.ATVA != null){
        grad =  response.OPDS;   
      }
      
      if(response.ATID.trim().equals("REC01") && response.ATVN != null){
        grwe = response.ATVN;  
      }
      
      if(response.ATID.trim().equals("REC02") && response.ATVN != null){
        tawe = response.ATVN;  
        newe = grwe.toDouble() - tawe.toDouble();
      }
      
      if(response.ATID.trim().equals("SUP01") && response.ATVA != null){
        suno = response.ATVA;  
      }
      
      if(response.ATID.trim().equals("REC04") && response.ATVA != null){
        loa1 = response.ATVA;  
        loam = response.OPDS;
      }
      
      if(response.ATID.trim().equals("REC05") && response.ATVA != null){
        car1 = response.ATVA;  
        carm = response.OPDS;
      }
      
      if(response.ATID.trim().equals("ADJ01") && response.ATVA != null){
        adj01x = response.OPDS;   
        adj01 = response.ATVA;
      }
      
      if(response.ATID.trim().equals("ADJ02") && response.ATVA != null){
        adj02x =  response.OPDS;  
        adj02 = response.ATVA;
      }
       
      if(response.ATID.trim().equals("ADJ03") && response.ATVA != null){
        adj03x =  response.OPDS;   
        adj03 = response.ATVA;
      }
       
      if(response.ATID.trim().equals("ADJ04") && response.ATVA != null){
        adj04x =  response.OPDS;  
        adj04 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ05") && response.ATVA != null){
        adj05x =  response.OPDS;    
        adj05 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ06") && response.ATVA != null){
        adj06x =  response.OPDS;  
        adj06 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ07") && response.ATVA != null){
        adj07x =  response.OPDS;   
        adj07 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ08") && response.ATVA != null){
        adj08x =  response.OPDS;    
        adj08 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ09") && response.ATVA != null){
        adj09x =  response.OPDS;  
        adj09 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ10") && response.ATVA != null){
        adj10x =  response.OPDS;    
        adj10 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ11") && response.ATVA != null){
        adj11x =  response.OPDS;   
        adj11 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ12") && response.ATVA != null){
        adj12x =  response.OPDS;    
        adj12 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ13") && response.ATVA != null){
        adj13x =  response.OPDS;   
        adj13 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ14") && response.ATVA != null){
        adj14x =  response.OPDS;    
        adj14 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ15") && response.ATVA != null){
        adj15x =  response.OPDS;   
        adj15 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ16") && response.ATVA != null){
        adj16x =  response.OPDS;    
        adj16 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ17") && response.ATVA != null){
        adj17x =  response.OPDS;    
        adj17 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ18") && response.ATVA != null){
        adj18x =  response.OPDS;   
        adj18 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ19") && response.ATVA != null){
        adj19x =  response.OPDS;   
        adj19 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ20") && response.ATVA != null){
        adj20x =  response.OPDS;    
        adj20 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ21") && response.ATVA != null){
        adj21x =  response.OPDS;   
        adj21 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ22") && response.ATVA != null){
        adj22x =  response.OPDS;   
        adj22 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ23") && response.ATVA != null){
        adj23x =  response.OPDS;    
        adj23 = response.ATVA;
      }
        
      if(response.ATID.trim().equals("ADJ24") && response.ATVA != null){
        adj24x =  response.OPDS;    
        adj24 = response.ATVA;
      }
      
      if(response.ATID.trim().equals("CHM04")){
        writeheader();
      }
    }
    
	  miCaller.call("ATS101MI","GetAttributes", params02, callback02);	
	 
  }
  
  
   
  /*
  * Write EXTCLX header record
  *
  */
  def writeheader() {
    
   def paramsx = ["SUNO":suno.toString()] // toString is needed to convert from gstring to string
   def callbackx = {
    Map<String, String> response ->
      if(response.SUNM != null){
        sunm = response.SUNM;  
      }
    }
    
    miCaller.call("CRS620MI","GetBasicData", paramsx, callbackx);	
      
    
    DBAction queryc = database.table("MPLINE").index("00").selection("IBCONO", "IBPUNO", "IBPNLI", "IBPNLS", "IBPUSL").build();
    DBContainer containerc = queryc.getContainer();
    containerc.set("IBCONO", XXCONO);
    containerc.set("IBPUNO", puno.toString());
    containerc.set("IBPNLI", pnli.toDouble());
    containerc.set("IBPNLS", 0);
    if (queryc.read(containerc)) {
      pusl = containerc.get("IBPUSL");
    }
    
    logger.debug('posting ' + pusl);
    
    ad1r = '';
    
    if(adj01 != null && adj01.trim() != 'N.') { ad1r += adj01x.trim() + ' - ';  }
    if(adj02 != null && adj02.trim() != 'N.') { ad1r += adj02x.trim() + ' - ';  }
    if(adj03 != null && adj03.trim() != 'N.') { ad1r += adj03x.trim() + ' - ';  }
    if(adj04 != null && adj04.trim() != 'N.') { ad1r += adj04x.trim() + ' - ';  }
    if(adj05 != null && adj05.trim() != 'N.') { ad1r += adj05x.trim() + ' - ';  }
    if(adj06 != null && adj06.trim() != 'N.') { ad1r += adj06x.trim() + ' - ';  }
    if(adj07 != null && adj07.trim() != 'N.') { ad1r += adj07x.trim() + ' - ';  }
    if(adj08 != null && adj08.trim() != 'N.') { ad1r += adj08x.trim() + ' - ';  }
    if(adj09 != null && adj09.trim() != 'N.') { ad1r += adj09x.trim() + ' - ';  }
    if(adj10 != null && adj10.trim() != 'N.') { ad1r += adj10x.trim() + ' - ';  }
    if(adj11 != null && adj11.trim() != 'N.') { ad1r += adj11x.trim() + ' - ';  }
    if(adj12 != null && adj12.trim() != 'N.') { ad1r += adj12x.trim() + ' - ';  }
    if(adj13 != null && adj13.trim() != 'N.') { ad1r += adj13x.trim() + ' - ';  }
    if(adj14 != null && adj14.trim() != 'N.') { ad1r += adj14x.trim() + ' - ';  }
    if(adj15 != null && adj15.trim() != 'N.') { ad1r += adj15x.trim() + ' - ';  }
    if(adj16 != null && adj16.trim() != 'N.') { ad1r += adj16x.trim() + ' - ';  }
    if(adj17 != null && adj17.trim() != 'N.') { ad1r += adj17x.trim() + ' - ';  }
    if(adj18 != null && adj18.trim() != 'N.') { ad1r += adj18x.trim() + ' - ';  }
    if(adj19 != null && adj19.trim() != 'N.') { ad1r += adj19x.trim() + ' - ';  }
    if(adj20 != null && adj20.trim() != 'N.') { ad1r += adj20x.trim() + ' - ';  }
    if(adj21 != null && adj21.trim() != 'N.') { ad1r += adj21x.trim() + ' - ';  }
    if(adj22 != null && adj22.trim() != 'N.') { ad1r += adj22x.trim() + ' - ';  }
    if(adj23 != null && adj23.trim() != 'N.') { ad1r += adj23x.trim() + ' - ';  }
    if(adj24 != null && adj24.trim() != 'N.') { ad1r += adj24x.trim() + ' - ';  }
    
    
    int currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger();
    int currentTime = Integer.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")));
       
    DBAction actionEXTCLX = database.table("EXTCLX").build();
    DBContainer EXTCLX = actionEXTCLX.getContainer();
    
    if (pusl != '99') {
    
      // Write EXTCLX header line of delivery docket
      EXTCLX.set("EXCONO", XXCONO);
      EXTCLX.set("EXDIVI", divi);
      EXTCLX.set("EXWHLO", whlo);
      EXTCLX.set("EXSUNO", suno);
      EXTCLX.set("EXSUM1", sunm);
      EXTCLX.set("EXSUDO", sudo);
      EXTCLX.set("EXGRAD", grad);
      EXTCLX.set("EXSEQ1", 0);
      EXTCLX.set("EXPUNO", puno);
      EXTCLX.set("EXPNLI", pnli.toDouble());
      EXTCLX.set("EXLTYP", 0);
      EXTCLX.set("EXLOTS", 1);
      EXTCLX.set("EXGRWE", grwe.toDouble());
      EXTCLX.set("EXTAWE", tawe.toDouble());
      EXTCLX.set("EXNEWE", newe.toDouble());
      EXTCLX.set("EXADJ1", ad1r);
      EXTCLX.set("EXCAR1", car1);
      EXTCLX.set("EXLOA1", loa1);
      EXTCLX.set("EXSUM1", carm);
      EXTCLX.set("EXSUM2", loam);
      EXTCLX.set("EXBREF", bref);
      EXTCLX.set("EXRGDT", currentDate);
      EXTCLX.set("EXRGTM", currentTime);
      EXTCLX.set("EXCHNO", 0);
      EXTCLX.set("EXCHID", program.getUser());
      actionEXTCLX.insert(EXTCLX, recordExists);
    }
  }
  /*
  * error if record exists
  *
  */
  Closure recordExists = {
	  mi.error("Record already exists");
  }
}
