/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.complaint.persistence.complaint.service;

import java.util.Date;
import java.util.List;

import org.complaint.domain.service.CrnGeneratorService;
import org.complaint.persistence.common.service.BoundaryService;
import org.complaint.persistence.common.service.DepartmentService;
import org.complaint.persistence.common.service.EmployeeService;
import org.complaint.persistence.complaint.entity.Complaint;
import org.complaint.persistence.complaint.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ComplaintService {

	@Autowired
	private CrnGeneratorService crnGeneratorService;

	@Autowired
	private ComplaintRepository complaintRepository;

	@Autowired
	private ComplaintTypeService complaintTypeService;

	@Autowired
	private ComplaintStatusService complaintStatusService;

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private BoundaryService boundaryService;

	@Autowired
	private DepartmentService departmentService;

	public List<Complaint> getAll() {

		return complaintRepository.findAll();
	}

	@Transactional
	public Complaint createComplaint(final Complaint complaint) {

		populateCrn(complaint);

		populateComplaintType(complaint);

		populateComplaintStatus(complaint);

		populateLocation(complaint);

		populateWorkflowUser(complaint);

		populateDepartment(complaint);

		setAuditableFields(complaint);

		complaintRepository.save(complaint);

		return complaint;
	}

	@Transactional
	public Complaint updateComplaint(Complaint complaint) {

		Complaint newComplaint = complaintRepository.getOne(complaint.getId());

		updateComplaintStatus(newComplaint, complaint.getStatus().getName());

		updateWorkflowUser(newComplaint, complaint.getAssignee().getId());

		setAuditableFields(newComplaint);

		complaintRepository.save(newComplaint);

		return newComplaint;
	}

	private void updateWorkflowUser(Complaint complaint, Long assignee) {
		if (assignee != null && assignee != null)
			complaint.setAssignee(employeeService.getById(assignee));

	}

	private void updateComplaintStatus(Complaint complaint, String name) {
		if (name != null && !name.isEmpty())
			complaint.setStatus(complaintStatusService.getByName(name));
	}

	private void populateDepartment(Complaint complaint) {
		if (complaint.getDepartment() != null && complaint.getDepartment().getCode() != null
				&& !complaint.getDepartment().getCode().isEmpty())
			complaint.setDepartment(departmentService.getByCode(complaint.getDepartment().getCode()));

	}

	private void populateWorkflowUser(Complaint complaint) {
		if (complaint.getAssignee() != null && complaint.getAssignee().getId() != null)
			complaint.setAssignee(employeeService.getById(complaint.getAssignee().getId()));

	}

	private void populateLocation(Complaint complaint) {
		if (complaint.getLatitude() != null && complaint.getLongitude() != null)
			complaint.setLocation(
					boundaryService.getByLongitudeAndLatitude(complaint.getLongitude(), complaint.getLatitude()));

	}

	private void populateComplaintStatus(Complaint complaint) {
		if (complaint.getStatus() != null && complaint.getStatus().getName() != null
				&& !complaint.getStatus().getName().isEmpty())
			complaint.setStatus(complaintStatusService.getByName(complaint.getStatus().getName()));

	}

	private void populateComplaintType(Complaint complaint) {

		if (complaint.getComplaintType() != null && complaint.getComplaintType().getCode() != null
				&& !complaint.getComplaintType().getCode().isEmpty())
			complaint.setComplaintType(complaintTypeService.getByCode(complaint.getComplaintType().getCode()));

	}

	private void populateCrn(Complaint complaint) {
		complaint.setCrn(crnGeneratorService.getCRN());
	}

	private void setAuditableFields(final Complaint complaint) {
		if (complaint.getCreatedBy() == null)
			complaint.setCreatedBy(1L);
		if (complaint.getCreatedDate() == null)
			complaint.setCreatedDate(new Date());
		complaint.setLastModifiedDate(new Date());
		complaint.setLastModifiedBy(1L);
	}

}
