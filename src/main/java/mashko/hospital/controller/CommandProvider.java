package mashko.hospital.controller;

import mashko.hospital.controller.command.Authorization;
import mashko.hospital.controller.command.FirstVisit;
import mashko.hospital.controller.command.SignOut;
import mashko.hospital.controller.command.admin.head.*;
import mashko.hospital.controller.command.admin.head.FindMedicationsPaging;
import mashko.hospital.controller.command.admin.head.FindProceduresPaging;
import mashko.hospital.controller.command.client.EditUserDetails;
import mashko.hospital.controller.command.client.FindUserDetails;
import mashko.hospital.controller.command.doctor.*;
import mashko.hospital.controller.command.receptionist.FindUserCredentials;
import mashko.hospital.controller.command.receptionist.RegisterClient;
import mashko.hospital.dao.impl.*;
import mashko.hospital.service.AdminHeadService;
import mashko.hospital.service.ClientService;
import mashko.hospital.service.DoctorService;
import mashko.hospital.service.ReceptionistService;
import mashko.hospital.service.impl.AdminHeadServiceImpl;
import mashko.hospital.service.impl.ClientServiceImpl;
import mashko.hospital.service.impl.DoctorServiceImpl;
import mashko.hospital.service.impl.ReceptionistServiceImpl;
import mashko.hospital.controller.command.admin.head.*;
import mashko.hospital.controller.command.doctor.*;
import mashko.hospital.dao.impl.*;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class CommandProvider {
    private final Map<CommandName, HttpCommand> map = new HashMap<>();

    public CommandProvider() {
        map.put(CommandName.FIRST_VISIT, new FirstVisit());
        map.put(CommandName.SIGN_OUT, new SignOut());
        map.put(CommandName.AUTHORIZATION,
                new Authorization(getClientService(),
                        Logger.getLogger(Authorization.class)));

        map.put(CommandName.FIND_USER_CREDENTIALS,
                new FindUserCredentials(getReceptionistService(),
                        Logger.getLogger(FindUserCredentials.class)));
        map.put(CommandName.REGISTER_CLIENT,
                new RegisterClient(getReceptionistService(),
                        Logger.getLogger(RegisterClient.class)));

        map.put(CommandName.CHANGE_DEPARTMENT_HEAD,
                new ChangeDepartmentHead(getAdminHeadService(),
                        Logger.getLogger(ChangeDepartmentHead.class)));
        map.put(CommandName.FIND_DEPARTMENT_CONTROL_ATTRIBUTES,
                new FindDepartmentControlAttributes(getAdminHeadService(),
                        Logger.getLogger(FindDepartmentControlAttributes.class)));
        map.put(CommandName.FIND_ROLE_CONTROL_ATTRIBUTES,
                new FindRoleControlAttributes(getAdminHeadService(),
                        Logger.getLogger(FindRoleControlAttributes.class)));
        map.put(CommandName.MOVE_DOCTOR_TO_DEPARTMENT,
                new MoveDoctorToDepartment(getAdminHeadService(),
                        Logger.getLogger(MoveDoctorToDepartment.class)));
        map.put(CommandName.ROLE_CONTROL,
                new RoleControl(getAdminHeadService(),
                        Logger.getLogger(RoleControl.class)));
        map.put(CommandName.PROCEDURE_CONTROL,
                new ProcedureControl(getAdminHeadService(),
                        Logger.getLogger(ProcedureControl.class)));
        map.put(CommandName.MEDICAMENT_CONTROL,
                new MedicamentControl(getAdminHeadService(),
                        Logger.getLogger(MedicamentControl.class)));
        map.put(CommandName.ADMIN_FIND_PROCEDURES_PAGING,
                new FindProceduresPaging(getAdminHeadService(),
                        Logger.getLogger(FindProceduresPaging.class)));
        map.put(CommandName.ADMIN_FIND_MEDICATIONS_PAGING,
                new FindMedicationsPaging(getAdminHeadService(),
                        Logger.getLogger(FindMedicationsPaging.class)));

        map.put(CommandName.FIND_USER_DETAILS,
                new FindUserDetails(getClientService(),
                        Logger.getLogger(FindUserDetails.class)));
        map.put(CommandName.EDIT_USER_DETAILS,
                new EditUserDetails(getClientService(),
                        Logger.getLogger(EditUserDetails.class)));

        map.put(CommandName.CLOSE_THERAPY,
                new CloseTherapy(getDoctorService(),
                        Logger.getLogger(CloseTherapy.class)));
        map.put(CommandName.DIAGNOSE_DISEASE,
                new DiagnoseDisease(getDoctorService(),
                        Logger.getLogger(DiagnoseDisease.class)));
        map.put(CommandName.FIND_OPEN_DOCTOR_THERAPIES,
                new FindOpenDoctorTherapies(getDoctorService(),
                        Logger.getLogger(FindOpenDoctorTherapies.class)));
        map.put(CommandName.FIND_PATIENT_THERAPIES,
                new FindPatientTherapies(getDoctorService(),
                        Logger.getLogger(FindPatientTherapies.class)));
        map.put(CommandName.MAKE_LAST_DIAGNOSIS_FINAL,
                new MakeLastDiagnosisFinal(getDoctorService(),
                        Logger.getLogger(MakeLastDiagnosisFinal.class)));
        map.put(CommandName.DOCTOR_FIND_MEDICATIONS_PAGING,
                new mashko.hospital.controller.command.doctor.FindMedicationsPaging(getDoctorService(),
                        Logger.getLogger(mashko.hospital.controller.command.doctor.FindMedicationsPaging.class)));
        map.put(CommandName.DOCTOR_FIND_PROCEDURES_PAGING,
                new mashko.hospital.controller.command.doctor.FindProceduresPaging(getDoctorService(),
                        Logger.getLogger(mashko.hospital.controller.command.doctor.FindProceduresPaging.class)));
        map.put(CommandName.FIND_ASSIGNMENT_MEDICATIONS,
                new FindAssignmentMedications(getDoctorService(),
                        Logger.getLogger(FindAssignmentMedications.class)));
        map.put(CommandName.FIND_ASSIGNMENT_PROCEDURES,
                new FindAssignmentProcedures(getDoctorService(),
                        Logger.getLogger(FindAssignmentProcedures.class)));
        map.put(CommandName.ASSIGN_MEDICAMENT,
                new AssignMedicament(getDoctorService(),
                        Logger.getLogger(AssignMedicament.class)));
        map.put(CommandName.ASSIGN_PROCEDURE,
                new AssignProcedure(getDoctorService(),
                        Logger.getLogger(AssignProcedure.class)));
    }

    public HttpCommand getCommand(CommandName command) {
        return map.get(command);
    }

    private ClientService getClientService() {
        return new ClientServiceImpl(new UserDaoImpl(),
                new UserDetailsDaoImpl());
    }

    private ReceptionistService getReceptionistService() {
        return new ReceptionistServiceImpl(new UserDaoImpl());
    }

    private AdminHeadService getAdminHeadService() {
        return new AdminHeadServiceImpl(new UserDaoImpl(),
                new DepartmentDaoImpl(),
                new DepartmentStaffDaoImpl(),
                new ProceduresDaoImpl(),
                new MedicamentDaoImpl());
    }

    private DoctorService getDoctorService() {
        return new DoctorServiceImpl(new IcdDaoImpl(),
                new UserDaoImpl(),
                new TherapyDaoImpl(),
                new DiagnosisDaoImpl(),
                new ProceduresDaoImpl(),
                new MedicamentDaoImpl());
    }
}
