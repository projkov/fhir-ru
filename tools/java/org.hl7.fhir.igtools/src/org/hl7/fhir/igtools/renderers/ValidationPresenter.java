package org.hl7.fhir.igtools.renderers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.dstu3.model.Constants;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.dstu3.utils.SimpleWorkerContext;
import org.hl7.fhir.dstu3.validation.ValidationMessage;
import org.hl7.fhir.igtools.publisher.FetchedFile;
import org.hl7.fhir.utilities.TextFile;
import org.stringtemplate.v4.ST;

public class ValidationPresenter implements Comparator<FetchedFile> {

  private SimpleWorkerContext context;

  public ValidationPresenter(SimpleWorkerContext context) {
    super();
    this.context = context;
  }

  private List<FetchedFile> sorted(List<FetchedFile> files) {
    List<FetchedFile> list = new ArrayList<FetchedFile>();
    list.addAll(files);
    Collections.sort(list, this);
    return list;
  }
  
  public String generate(String title, List<ValidationMessage> linkErrors, List<FetchedFile> files, String path) throws IOException {
    StringBuilder b = new StringBuilder();
    b.append(genHeader(title));
    files = sorted(files);
    for (FetchedFile f : files) 
      b.append(genSummaryRow(f));
    b.append(genEnd());
    for (FetchedFile f : files) {
      b.append(genStart(f));
      for (ValidationMessage vm : f.getErrors())
        b.append(genDetails(vm));
      b.append(genEnd());
    }    
    b.append(genFooter(title));
    TextFile.stringToFile(b.toString(), path);
    return path;
  }

  // HTML templating
  private final String headerTemplate = 
      "<!DOCTYPE HTML>\r\n"+
      "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\r\n"+
      "<head>\r\n"+
      "  <title>$title$ : Validation Results</title>\r\n"+
      "  <link href=\"fhir.css\" rel=\"stylesheet\"/>\r\n"+
      "</head>\r\n"+
      "<body style=\"margin: 20px; background-color: #ffffff\">\r\n"+
      " <h1>Validation Results for $title$</h1>\r\n"+
      " <p>Generated $time$. FHIR version $version$</p>\r\n"+
      " <table class=\"grid\">\r\n"+
      "   <tr>\r\n"+
      "     <td><b>Filename</b></td><td><b>Errors</b></td><td><b>Hints &amp; Warnings</b></td>\r\n"+
      "   </tr>\r\n";
  
  private final String summaryTemplate = 
      "   <tr style=\"background-color: $color$\">\r\n"+
      "     <td><a href=\"#$link$\"><b>$filename$</b></a></td><td><b>$errcount$</b></td><td><b>$other$</b></td>\r\n"+
      "   </tr>\r\n";
  
  private final String endTemplate = 
      "</table>\r\n";

  private final String startTemplate = 
      "<hr/>\r\n"+
      "<a name=\"$link$\"> </a>\r\n"+
      "<h2>$path$</h2>\r\n"+
      " <table class=\"grid\">\r\n"+
      "   <tr>\r\n"+
      "     <td><b>Path</b></td><td><b>Severity</b></td><td><b>Message</b></td>\r\n"+
      "   </tr>\r\n";

  private final String detailsTemplate = 
      "   <tr style=\"background-color: $color$\">\r\n"+
      "     <td><b>$path$</b></td><td><b>$level$</b></td><td><b>$msg$</b></td>\r\n"+
      "   </tr>\r\n";
  
  private final String footerTemplate = 
      "</body>\r\n"+
      "</html>\r\n";

  private ST template(String t) {
    return new ST(t, '$', '$');
  }

  private String genHeader(String title) {
    ST t = template(headerTemplate);
    t.add("version", Constants.VERSION);
    t.add("title", title);
    t.add("time", new Date().toString());
    return t.render();
  }

  private String genEnd() {
    ST t = template(endTemplate);
    t.add("version", Constants.VERSION);
    t.add("time", new Date().toString());
    return t.render();
  }

  private String genFooter(String title) {
    ST t = template(footerTemplate);
    t.add("version", Constants.VERSION);
    t.add("title", title);
    t.add("time", new Date().toString());
    return t.render();
  }

  private String genSummaryRow(FetchedFile f) {
    ST t = template(summaryTemplate);
    t.add("link", makelink(f));
    
    t.add("filename", f.getName());
    String ec = errCount(f);
    t.add("errcount", ec);
    t.add("other", otherCount(f));
    if ("0".equals(ec))
      t.add("color", "#EFFFEF");
    else
      t.add("color", colorForLevel(IssueSeverity.ERROR));
      
    return t.render();
  }

  private String makelink(FetchedFile f) {
    return f.getName().replace("/", "_").replace("\\", "_");
  }

  private String errCount(FetchedFile f) {
    int c = 0;
    for (ValidationMessage vm : f.getErrors()) {
      if (vm.getLevel() == IssueSeverity.ERROR || vm.getLevel() == IssueSeverity.FATAL)
        c++;
    }
    return Integer.toString(c);
  }

  private Object otherCount(FetchedFile f) {
    int c = 0;
    for (ValidationMessage vm : f.getErrors()) {
      if (vm.getLevel() == IssueSeverity.INFORMATION || vm.getLevel() == IssueSeverity.WARNING)
        c++;
    }
    return Integer.toString(c);
  }

  private String genStart(FetchedFile f) {
    ST t = template(startTemplate);
    t.add("link", makelink(f));
    t.add("filename", f.getName());
    t.add("path", f.getPath());
    return t.render();
  }
  private String genDetails(ValidationMessage vm) {
    ST t = template(detailsTemplate);
    t.add("path", vm.getLocation());
    t.add("level", vm.getLevel().toCode());
    t.add("color", colorForLevel(vm.getLevel()));
    t.add("msg", vm.getHtml());
    return t.render();
  }

  private String colorForLevel(IssueSeverity level) {
    switch (level) {
    case ERROR:
      return "#ffcccc";
    case FATAL:
      return "#ff9999";
    case WARNING:
      return "#ffebcc";
    default: // INFORMATION:
      return "#ffffe6";
    }
  }

  @Override
  public int compare(FetchedFile f1, FetchedFile f2) {
    return f1.getName().compareTo(f2.getName());
  }

  
}