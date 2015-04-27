package org.hl7.fhir.instance.validation;

/*
Copyright (c) 2011+, HL7, Inc
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
   list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, 
   this list of conditions and the following disclaimer in the documentation 
   and/or other materials provided with the distribution.
 * Neither the name of HL7 nor the names of its contributors may be used to 
   endorse or promote products derived from this software without specific 
   prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
POSSIBILITY OF SUCH DAMAGE.

*/

import java.util.List;

import org.hl7.fhir.instance.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.instance.validation.ValidationMessage.Source;

public class BaseValidator {

  protected Source source;
  
  protected boolean fail(List<ValidationMessage> errors, String type, int line, int col, String path, boolean b, String msg) {
    if (!b)
      errors.add(new ValidationMessage(source, type, line, col, path, msg, IssueSeverity.FATAL));
    return b;
  }

  
  protected boolean rule(List<ValidationMessage> errors, String type, int line, int col, String path, boolean b, String msg) {
  	if (!b)
  		errors.add(new ValidationMessage(source, type, line, col, path, msg, IssueSeverity.ERROR));
  	return b;
  }

  protected boolean hint(List<ValidationMessage> errors, String type, int line, int col, String path, boolean b, String msg) {
    if (!b)
      errors.add(new ValidationMessage(source, type, line, col, path, msg, IssueSeverity.INFORMATION));
    return b;
  }

  protected boolean warning(List<ValidationMessage> errors, String type, int line, int col, String path, boolean b, String msg) {
    if (!b)
      errors.add(new ValidationMessage(source, type, line, col, path, msg, IssueSeverity.WARNING));
    return b;
    
  }

  protected boolean fail(List<ValidationMessage> errors, String type, String path, boolean b, String msg) {
    if (!b)
      errors.add(new ValidationMessage(source, type, -1, -1, path, msg, IssueSeverity.FATAL));
    return b;
  }

  
  protected boolean rule(List<ValidationMessage> errors, String type, String path, boolean b, String msg) {
    if (!b)
      errors.add(new ValidationMessage(source, type, -1, -1, path, msg, IssueSeverity.ERROR));
    return b;
  }

  protected boolean hint(List<ValidationMessage> errors, String type, String path, boolean b, String msg) {
    if (!b)
      errors.add(new ValidationMessage(source, type, -1, -1, path, msg, IssueSeverity.INFORMATION));
    return b;
  }

  protected boolean warning(List<ValidationMessage> errors, String type, String path, boolean b, String msg) {
    if (!b)
      errors.add(new ValidationMessage(source, type, -1, -1, path, msg, IssueSeverity.WARNING));
    return b;
    
  }

}
