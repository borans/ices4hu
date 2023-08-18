package com.pointers.ices4hu.services;

import com.pointers.ices4hu.models.*;
import com.pointers.ices4hu.repositories.CourseEnrolmentRequestRepository;
import com.pointers.ices4hu.repositories.CourseRepository;
import com.pointers.ices4hu.repositories.SurveyRepository;
import com.pointers.ices4hu.requests.AddCourseRequest;
import com.pointers.ices4hu.requests.AssignInstructorPair;
import com.pointers.ices4hu.requests.AssignInstructorRequest;
import com.pointers.ices4hu.responses.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final SurveyRepository surveyRepository;
    private final UserService userService;
    private final DepartmentService departmentService;
    private final CourseEnrolmentRequestRepository courseEnrolmentRequestRepository;

    public CourseService(CourseRepository courseRepository,
                         SurveyRepository surveyRepository,
                         UserService userService,
                         DepartmentService departmentService,
                         CourseEnrolmentRequestRepository courseEnrolmentRequestRepository) {
        this.courseRepository = courseRepository;
        this.surveyRepository = surveyRepository;
        this.userService = userService;
        this.departmentService = departmentService;
        this.courseEnrolmentRequestRepository = courseEnrolmentRequestRepository;
    }

    public List<CourseResponse> getCourseResponses(String loginID) {
        User user = userService.getUserByLoginID(loginID);
        Set<Course> courses = user.getCourses();

        /*List<CourseResponse> courseResponses = new ArrayList<>();
        for (Course course: courses) {
            CourseResponse courseResponse = new CourseResponse();

            courseResponse.setId(course.getId());
            courseResponse.setDepartment(course.getDepartment().getName());
            courseResponse.setCode(course.getCode());
            courseResponse.setName(course.getName());

            if (course.getMandatory())
                courseResponse.setCourseType("Mandatory");
            else
                courseResponse.setCourseType("Elective");

            courseResponse.setCredit(course.getCredit());

            if (course.getUndergraduate())
                courseResponse.setCourseDegree("Undergraduate");
            else
                courseResponse.setCourseDegree("Graduate");

            courseResponse.setInstructorName(course.getUser().getFullName());

            List<Survey> surveys = surveyRepository.findByCourseId(course.getId());
            surveys.addAll(surveyRepository.findByInstructorId(course.getUser().getId()));

            courseResponse.setEvaluationFormStatus(
                    String.format("%d/%d",
                            userService.getNumberOfSurveysFilledByUser(surveys, loginID),
                            surveys.size()
                    )
            );

            courseResponses.add(courseResponse);

        }*/

        List<Course> sortedCourses = new ArrayList<>();
        sortedCourses.addAll(courses);
        Collections.sort(sortedCourses, CourseComparator.getInstance());

        List<CourseResponse> courseResponses = getCourseResponsesFromCourseList(sortedCourses,
                true, true, loginID);

        return courseResponses;
    }

    public List<Course> getCourses(String loginID) {
        User user = userService.getUserByLoginID(loginID);
        return user.getCourses().stream().toList();
    }

    public Course getCourseByCourseId(Long courseId) {
        return courseRepository.findById(courseId).orElse(null);
    }

    public List<CourseResponse> getCourseResponsesOfInstructor(String instructorLoginId) {
        List<Course> courses = courseRepository.findCoursesByInstructorLoginId(instructorLoginId);
        Collections.sort(courses, CourseComparator.getInstance());
        List<CourseResponse> courseResponses = getCourseResponsesFromCourseList(courses,
                false, false, null);

        return courseResponses;
    }

    public List<CourseResponse> getCourseResponsesOfAdmin() {
        List<Course> courses = courseRepository.findAll();
        Collections.sort(courses, CourseComparator.getInstance());
        List<CourseResponse> courseResponses = getCourseResponsesFromCourseList(courses,
                true, false, null);
        return courseResponses;
    }

    public DepartmentManagerCourseResponse getCourseResponsesOfDepartmentManager(String loginID) {
        User departmentManager = userService.getUserByLoginID(loginID);
        Department department = departmentManager.getDepartment();

        List<Course> courses = courseRepository.findCoursesByDepartmentId(department.getId());
        Collections.sort(courses, CourseComparator.getInstance());
        List<CourseResponseWithInstructorId> courseResponseWithInstructorIdList = new ArrayList<>();

        for (Course course: courses) {
            CourseResponseWithInstructorId courseResponseWithInstructorId = new CourseResponseWithInstructorId();
            courseResponseWithInstructorId.buildFrom(course);
            courseResponseWithInstructorIdList.add(courseResponseWithInstructorId);
        }

        /*List<CourseResponse> courseResponses = getCourseResponsesFromCourseList(courses,
                true, false, null);*/

        List<User> instructors = userService.findInstructorsAtDepartment(department.getId());
        List<InstructorResponse> instructorResponses = new ArrayList<>();
        for (User user: instructors) {
            InstructorResponse instructorResponse = new InstructorResponse();
            instructorResponse.buildFrom(user);
            instructorResponses.add(instructorResponse);
        }


        DepartmentManagerCourseResponse departmentManagerCourseResponse = new DepartmentManagerCourseResponse();
        departmentManagerCourseResponse.setCourses(courseResponseWithInstructorIdList);
        departmentManagerCourseResponse.setInstructors(instructorResponses);

        return departmentManagerCourseResponse;
    }

    private List<CourseResponse> getCourseResponsesFromCourseList(Iterable<Course> courses,
                                                                  boolean includeInstructor,
                                                                  boolean includeEvaluationFormStatus,
                                                                  String loginID) {
        List<CourseResponse> courseResponses = new ArrayList<>();
        for (Course course: courses) {
            CourseResponse courseResponse = new CourseResponse();
            courseResponse.buildFrom(course);
            /*courseResponse.setId(course.getId());
            courseResponse.setDepartment(course.getDepartment().getName());

            courseResponse.setCode(course.getCode());
            courseResponse.setName(course.getName());

            if (course.getMandatory())
                courseResponse.setCourseType("Mandatory");
            else
                courseResponse.setCourseType("Elective");

            courseResponse.setCredit(course.getCredit());

            if (course.getUndergraduate())
                courseResponse.setCourseDegree("Undergraduate");
            else
                courseResponse.setCourseDegree("Graduate");
            */

            if (!includeInstructor)
                courseResponse.setInstructorName(null);

            if (includeEvaluationFormStatus) {
                List<Survey> surveys = surveyRepository.findByCourseId(course.getId());
                int createdSurveyCounter = 0;
                for (Survey survey: surveys) {
                    if (survey.getCreationDatetime() != null) {
                        createdSurveyCounter++;
                    }
                }
                // surveys.addAll(surveyRepository.findByInstructorId(course.getUser().getId()));

                courseResponse.setEvaluationFormStatus(
                        String.format("%d/%d",
                                userService.getNumberOfSurveysFilledByUser(surveys, loginID),
                                createdSurveyCounter
                        )
                );
            }

            courseResponses.add(courseResponse);

        }

        return courseResponses;
    }

    public List<Course> getCoursesByInstructorLoginId(String instructorLoginId) {
        return courseRepository.findCoursesByInstructorLoginId(instructorLoginId);
    }


    public ResponseEntity<Object> assignInstructors(String user, AssignInstructorRequest assignInstructorRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(user))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        List<AssignInstructorPair> pairs = assignInstructorRequest.getPairs();

        for(AssignInstructorPair pair: pairs) {
            Long courseId = pair.getCourseId();
            Long instructorId = pair.getInstructorId();

            Course course = courseRepository.findById(courseId).orElse(null);
            if (course == null)
                continue;

            User instructor = userService.getUser(instructorId);
            if (instructor == null)
                continue;

            if (course.getUser() != null && course.getUser().getId() != null
            && course.getUser().getId() != instructorId) {
                int count = surveyRepository.findByInstructorId(course.getUser().getId()).size();
                if (count > 0) {
                    return new ResponseEntity<>(new MessageResponse("The relation of a course with an instructor " +
                            "having surveys cannot be removed!: " + course.getUser().getFullName()),
                            HttpStatus.BAD_REQUEST);
                }
            }

            course.setUser(instructor);
            courseRepository.save(course);
        }

        return new ResponseEntity<>(new MessageResponse("Operation successful!"), HttpStatus.OK);

    }

    public ResponseEntity<Object> createCourse(AddCourseRequest addCourseRequest) {
        String name = addCourseRequest.getName();
        String code = addCourseRequest.getCode();
        Long departmentId = addCourseRequest.getDepartmentId();
        Integer credit = addCourseRequest.getCredit();
        Boolean undergraduate = addCourseRequest.getUndergraduate();
        Boolean mandatory = addCourseRequest.getMandatory();

        if (!StringUtils.hasText(name)) {
            return new ResponseEntity<>(new MessageResponse("Name cannot be empty!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (!StringUtils.hasText(code)) {
            return new ResponseEntity<>(new MessageResponse("Course code cannot be empty!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (credit == null) {
            return new ResponseEntity<>(new MessageResponse("Credit cannot be empty!"),
                    HttpStatus.BAD_REQUEST);
        }

        List<Course> courses = courseRepository.findCoursesByCode(code);
        if (courses.size() > 0) {
            return new ResponseEntity<>(new MessageResponse("Course already exists!"),
                    HttpStatus.BAD_REQUEST);
        }

        Department department = departmentService.getDepartmentByDepartmentId(departmentId);
        if (department == null) {
            return new ResponseEntity<>(new MessageResponse("No such department!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (credit < 0) {
            return new ResponseEntity<>(new MessageResponse("Credit must not be negative!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (undergraduate == null) {
            return new ResponseEntity<>(new MessageResponse("No undergraduate information!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (mandatory == null) {
            return new ResponseEntity<>(new MessageResponse("No mandatory information!"),
                    HttpStatus.BAD_REQUEST);
        }

        Course course = new Course();
        course.setUser(null); // the instructor will be set by the department manager
        course.setCredit(credit);
        course.setCode(code);
        course.setDepartment(department);
        course.setName(name);
        course.setMandatory(mandatory);
        course.setUndergraduate(undergraduate);

        courseRepository.save(course);

        return new ResponseEntity<>(new MessageResponse("The course has successfully been created!"),
                HttpStatus.CREATED);

    }

    public ResponseEntity<Object> deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);

        if (course == null)
            return new ResponseEntity<>(new MessageResponse("No such course!"),
                    HttpStatus.BAD_REQUEST);

        List<CourseEnrolmentRequest> courseEnrolmentRequests = courseEnrolmentRequestRepository
                .getCourseEnrolmentRequestsByCourseId(courseId);

        ResponseEntity<Object> cannotDeleteResponse = new ResponseEntity<>(
                new MessageResponse("Cannot delete a course being used in the system!"), HttpStatus.BAD_REQUEST);

        if (courseEnrolmentRequests.size() > 0)
            return cannotDeleteResponse;

        List<Survey> survey = surveyRepository.findByCourseId(courseId);
        if (survey.size() > 0)
            return cannotDeleteResponse;

        List<User> users = userService.getAllUsers();
        for (User user: users) {
            if (user.getCourses().contains(course))
                return cannotDeleteResponse;
        }

        courseRepository.delete(course);
        return new ResponseEntity<>(new MessageResponse("Operation successful!"),
                HttpStatus.OK);

    }

    public ResponseEntity<Object> editCourse(Long courseId, AddCourseRequest addCourseRequest) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return new ResponseEntity<>(new MessageResponse("No such course!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (!StringUtils.hasText(addCourseRequest.getName())) {
            return new ResponseEntity<>(new MessageResponse("Course name cannot be empty!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (addCourseRequest.getCredit() == null) {
            return new ResponseEntity<>(new MessageResponse("Credit field cannot be empty!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (addCourseRequest.getCredit() < 0) {
            return new ResponseEntity<>(new MessageResponse("Credit cannot be negative!"),
                    HttpStatus.BAD_REQUEST);
        }

        course.setName(addCourseRequest.getName());
        course.setCredit(addCourseRequest.getCredit());
        courseRepository.save(course);
        return new ResponseEntity<>(new MessageResponse("Operation successful!"),
                HttpStatus.OK);

    }
}

