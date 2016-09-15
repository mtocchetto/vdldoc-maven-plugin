# vdldoc-maven-plugin

Maven plugin designed for enable maven users to create javadocs based on taglibs xml files using the the class "org.omnifaces.vdldoc.VdldocGenerator" provided by the Vdldoc project available at http://vdldoc.omnifaces.org  

## Command line help

    mvn com.tocchetto:vdldoc-maven-plugin:1.0.0-SNAPSHOT:help -Ddetail=true -Dgoal=generate 

## Command line example

    mvn com.tocchetto:vdldoc-maven-plugin:1.0.0-SNAPSHOT:generate -DmanualTaglibs=primefaces-p.taglib.xml -DoutputDirectory=vdldoc-primefaces
