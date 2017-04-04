/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) 2016  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.complaint.errorhandlers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.complaint.web.contract.ResponseInfo;
import org.complaint.web.contract.factory.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Component
public class ErrorHandler {

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	public ResponseEntity<ErrorResponse> getErrorResponseEntityForBindingErrors(BindingResult bindingResult) {
		Error error = new Error();
		error.setCode(400);
		error.setMessage("Binding Error");
		error.setDescription("Error while binding request object");

		if (bindingResult.hasFieldErrors()) {
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				if (fieldError.getField().contains("Date")) {
					DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
					String errorDate = dateFormat.format(fieldError.getRejectedValue());
					error.getFields().put(fieldError.getField(), errorDate);
				} else {
					error.getFields().put(fieldError.getField(), fieldError.getRejectedValue());
				}
			}
		}

		ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(new Date().toString(), false);

		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setResponseInfo(responseInfo);
		errorResponse.setError(error);

		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
	}
}
