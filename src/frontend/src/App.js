import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

import Login from './components/login/login';
import Signup from './components/signup/signup';
import ForgotPassword from './components/forgotPassword/forgotPassword';
import DeleteUser from './components/deleteuser/deleteuser';
import DeleteSelfDM from "./departmentManager/deleteSelfDM/deleteSelfDM";
import DeleteSelfInstructor from "./instructor/deleteSelfInstructor/deleteSelfInstructor";
import DeleteSelfStudent from "./student/deleteSelfStudent/deleteSelfStudent";
import SurveyEditAdmin from './admin/surveyEditAdmin/surveyEditAdmin';
import MainPageStudent from './student/mainpageStudent/mainpageStudent';
import StudentSurveys from './student/studentSurveys/studentSurveys';
import StudentCourses from './student/studentCourses/studentCourses';
import ScheduleStudent from './student/scheduleStudent/scheduleStudent';
import SurveyForm from './student/surveyForm/surveyForm';
import SettingsStudent from './student/settingsStudent/settingsStudent';
import SettingsInstructor from './instructor/settingsIns/settingsIns';
import SettingsDM from './departmentManager/settingsDM/settingsDM';
import NewsletterDMCreate from './departmentManager/newsletter/postCreate';
import NewsletterDMView from './departmentManager/newsletter/viewPosts';
import NewsletterDMEdit from './departmentManager/newsletter/postEdit';
import NewsletterInsView from './instructor/newsletter/viewPosts';
import ViewNewsletterPostInstructor from './instructor/newsletter/viewPost';
import SettingsAdmin from './admin/settingsAdmin/settingsAdmin';
import ScheduleInstructor from './instructor/scheduleInstructor/scheduleInstructor';
import DMSchedule from './departmentManager/scheduleDM/scheduleDM';
import MainPageInstructor from "./instructor/mainPageInstructor/mainpageInstructor";
import InstructorCourses from "./instructor/instructorCourses/instructorCourses";
import InstructorSurveys from "./instructor/instructorSurveys/instructorSurveys";
import InstructorQuestionBank from "./instructor/instructorQuestionBank/instructorQB";
import ViewStatsInstructor from "./instructor/viewStatsInstructor/viewStatsInstructor";
import MainPageManager from './departmentManager/mainpageManager/mainpageManager';
import ManagerCourses from './departmentManager/managerCourses/managerCourses';
import ManagerSurveys from './departmentManager/managerSurveys/managerSurveys';
import ViewStatsDepartmentManager from "./departmentManager/viewStatsManager/viewStatsManager";
import CourseReg from './student/courseRegistration/courseRegistration';
import MainPageAdmin from './admin/mainpageAdmin/mainpageAdmin';
import AdminCourses from './admin/adminCourses/adminCourses';
import AdminSurveys from './admin/adminSurveys/adminSurveys';
import ViewStatsAdmin from "./admin/viewStatsAdmin/viewStatsAdmin";
import AdminCourseEnrolment from './admin/courseEnrolmentRequest/courseEnrolmentRequest';
import AdminUserEnrolment from './admin/userEnrolmentRequests/userEnrolmentRequest';
import AdminAddInstructor from './admin/addInstructorAdmin/addInstructor';
import SurveyCreate from './instructor/surveyCreate/surveyCreate';
import SurveyEdit from './instructor/surveyEdit/surveyEdit.jsx';
import SurveySubmittersList from './departmentManager/SurveySubmittersList/SurveySubmittersList';
import AdminSchedule from './admin/adminSchedule/adminSchedule';
import ViewSurveyInstructor from './instructor/viewSurveyInstructor/viewSurveyInstructor';
import ViewSurveyDM from './departmentManager/viewSurveyDM/viewSurveyDM';
import ViewStudentAnswers from './departmentManager/viewStudentAnswers/viewStudentAnswers';
import ViewSurveyAdmin from './admin/viewSurveyDM/viewSurveyAdmin';

import "./App.css"

const App = () => {
  return (
    <div className='App'>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/forgotPassword" element={<ForgotPassword />} />

          <Route path="/deleteAccount" element= {<DeleteUser />}/>
          <Route path="/deleteAccountDM" element= {<DeleteSelfDM />}/>
          <Route path="/deleteAccountInstructor" element= {<DeleteSelfInstructor />}/>
          <Route path="/deleteAccountStudent" element= {<DeleteSelfStudent />}/>

          <Route path="/mainPageStudent" element={<MainPageStudent />} />
          <Route path="/studentCourses" element={<StudentCourses />} />
          <Route path="/studentSurveys" element={<StudentSurveys />} />
          <Route path="/survey/:surveyID" element= {<SurveyForm />}/>
          <Route path="/studentCourseReg" element={<CourseReg />} />
          <Route path="/accountSettingsStudent" element={<SettingsStudent />} />
          <Route path="/accountSettingsIns" element={<SettingsInstructor />} />
          <Route path="/accountSettingsDM" element={<SettingsDM />} />
          <Route path="/accountSettingsAdmin" element={<SettingsAdmin />} />

          <Route path="/mainPageInstructor" element={<MainPageInstructor />} />
          <Route path="/instructorCourses" element={<InstructorCourses />} />
          <Route path="/instructorSurveys" element={<InstructorSurveys />} />
		      <Route path="/editSurvey/:surveyID" element={<SurveyEdit />} />
		      <Route path="/viewSurveyInstructor/:surveyID" element={<ViewSurveyInstructor />} />
          <Route path="/instructorQB" element={<InstructorQuestionBank />} />
          <Route path="/viewStatsInstructor/:surveyID" element={<ViewStatsInstructor />} />
		      
          <Route path="/viewSurveyDM/:surveyID" element={<ViewSurveyDM />} />
          <Route path="/viewSurveyAnswers/:surveyID/:studentUserName" element={<ViewStudentAnswers />} />
          <Route path="/mainPageManager" element={<MainPageManager />} />
          <Route path="/managerCourses" element={<ManagerCourses />} />
          <Route path="/managerSurveys" element={<ManagerSurveys />} />
		      <Route path="/surveySubmitters/:surveyID" element={<SurveySubmittersList />} />
          <Route path="/viewStatsDM/:surveyID" element={<ViewStatsDepartmentManager />} />

		      <Route path="/editSurveyAdmin/:surveyID" element={<SurveyEditAdmin />} />
          <Route path="/viewSurveyAdmin/:surveyID" element={<ViewSurveyAdmin />} />
          <Route path="/mainPageAdmin" element={<MainPageAdmin />} />
          <Route path="/adminCourses" element={<AdminCourses />} />
          <Route path="/adminSurveys" element={<AdminSurveys />} />
          <Route path="/viewStatsAdmin/:surveyID" element={<ViewStatsAdmin />} />
          <Route path="/adminSchedule" element={<AdminSchedule />} />
          <Route path="/courseEnrolmentRequest" element={<AdminCourseEnrolment />} />
		      <Route path="/userEnrolmentRequests" element={<AdminUserEnrolment />} />
          <Route path="/viewUserAndAddInstructor" element={<AdminAddInstructor />} />
          <Route path="/createSurvey" element={<SurveyCreate />} />

          <Route path="/studentSchedule" element={<ScheduleStudent />} />
          <Route path="/managerSchedule" element={<DMSchedule />} />
          <Route path="/instructorSchedule" element={<ScheduleInstructor />} />
          <Route path="/viewScheduleStudent" element={<ScheduleStudent />} />
          <Route path="/createPost" element={<NewsletterDMCreate />} />
          <Route path="/instructorNR" element={<NewsletterInsView />} />
          <Route path="/managerNR" element={<NewsletterDMView />} />
          <Route path="/editPost/:postId" element={<NewsletterDMEdit />} />
          <Route path="/viewPostInstructor/:postId" element={<ViewNewsletterPostInstructor />} />

        </Routes>
      </BrowserRouter>
    </div>
  );
};

export default App;