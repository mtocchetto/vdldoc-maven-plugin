package com.tocchetto.vdldoc;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.StringUtils;
import org.omnifaces.vdldoc.VdldocGenerator;

@Mojo( name = "generate", requiresProject = false ) // defaultPhase = LifecyclePhase.PREPARE_PACKAGE, 
public class VdldocGeneratorMojo extends AbstractMojo {
	
	/**
	 * The browser window title for the VDL documentation.
	 */
	@Parameter( property = "windowTitle" )
	private String windowTitle;
	
	/**
	 * The title for the VDL index page.
	 */
	@Parameter( property = "docTitle" )
	private String docTitle;

	/**
	 * The output directory for generated files.
	 */
	@Parameter( property = "outputDirectory" )
    private String outputDirectory;
	
	/**
	 * The location of the CSS file.
	 */
	@Parameter( property = "cssLocation" )
	private String cssLocation;

	/**
	 * The faces config file we are parsing.
	 */
	@Parameter( property = "facesConfig" )
	private File facesConfig;
	
	/**
	 * The properties file for implied attributes of composite components.
	 */
	@Parameter( property = "attributes" )
	private File attributes;
	
	/**
	 * True if "Output generated by Vdldoc" footer must be hidden.
	 */
	@Parameter( property = "hideGeneratedBy" , defaultValue = "false" )
	private Boolean hideGeneratedBy;
	
	/**
	 * The base directory to search for tag library files.
	 */
	@Parameter( property = "searchDirectory" )
	private String searchDirectory;
	
	/**
	 * <p>List of tag library file names, which should be used to
	 * be parsed.</p> 
	 * <p>Supports patterns at the same way of ant build scripts, described in {@link DirectoryScanner} class supplied by "Plexus Utils" library.</p>
	 * <p>Patterns: 
	 * <ul>
	 * <li>'*' matches zero or more characters</li> 
	 * <li>'?' matches one character.</li>
	 * </ul>
	 * </p>
	 */
	@Parameter( property = "taglibs", defaultValue = "*taglib.xml" )
	private String[] taglibs;

	/**
	 * Comma-separated list of tag library file names, which should be used to
	 * be parsed. This is a command-line alternative to the taglibs parameter,
	 * since List parameters are not currently compatible with CLI
	 * specification.
	 */
	@Parameter( property = "manualTaglibs", defaultValue = "*taglib.xml" )
	private String manualTaglibs;
	
	/**
	 * True if no stdout is to be produced during generation.
	 */
	@Parameter( property = "quiet" , defaultValue = "false" )
	private Boolean quiet;
	
	@Parameter( readonly = true ) // defaultValue = "${project}" 
	private MavenProject project;

	public void execute() throws MojoExecutionException {
		try {
			VdldocGenerator generator = new VdldocGenerator();
			
			if(project == null) {
				if(isBlank(searchDirectory)) {
				//	getLog().warn("The searchDirectory should be informed at command-line mode.");
				}
				if(isNotBlank(manualTaglibs)) {
					taglibs = manualTaglibs.split(",");
				}
			} else {
				outputDirectory = project.getBuild().getOutputDirectory() + "/" + (outputDirectory == null ? "vdldoc" : outputDirectory);
				searchDirectory = project.getBasedir() + "/" + (searchDirectory == null ? "src/main/resources" : searchDirectory);
			}
			// if(isNotBlank(searchDirectory) && taglibs != null && taglibs.length > 0) {
			if(taglibs != null && taglibs.length > 0) {
				File baseDir = isBlank(searchDirectory) ? new File(".") : new File(searchDirectory);
				
				
				final DirectoryScanner scanner = new DirectoryScanner();
				scanner.setBasedir(baseDir);
				scanner.setIncludes(taglibs);
				scanner.setCaseSensitive(false);
				scanner.scan();
				
				final String[] fileNames = scanner.getIncludedFiles();
				for (final String fileName : fileNames) {
					logInfo("Adding file '" + fileName +"'...");
					generator.addTaglib(new File(fileName));
				}
			}
			if(isNotBlank(windowTitle)) {
				generator.setWindowTitle(windowTitle);
			}
			if(isNotBlank(docTitle)) {
				generator.setDocTitle(docTitle); 
			}
			if(outputDirectory != null) {
				generator.setOutputDirectory(new File(outputDirectory)); 
			}
			if(isNotBlank(cssLocation)) {
				generator.setCssLocation(cssLocation);
			}
			if(facesConfig != null) {
				generator.setFacesConfig(facesConfig);
			}
			if(attributes != null) {
				generator.setAttributes(attributes);
			}
			if(hideGeneratedBy != null) {
				generator.setHideGeneratedBy(hideGeneratedBy);
			}
			
			logInfo("Generating VDL documentation...");
			generator.generate();
			logInfo("VDL documentation generated with success!");
		} catch (final Exception e) {
			throw new MojoExecutionException("Error creating VDL documentation.", e);
		}
	}
	
	protected void logInfo(final String message) {
		if(Boolean.FALSE.equals(quiet)) {
			getLog().info(message);
		}
	}

	protected boolean isBlank(final String input) {
		return input == null || input.trim().isEmpty();
	}

	protected boolean isNotBlank(final String input) {
		return input != null && !input.trim().isEmpty();
	}

}