import React from "react";
import TopBar from "../topbarInstructor/topbarInstructor";
import { useNavigate } from "react-router-dom";
import {logout} from "../../components/logout";
import SideBar from "../sidebarInstructor/sidebarInstructor";
import styles from "./mainpageInstructor.module.css"
import myCourses_logo from '../../assets/myCourses_logo.jpg';
import qb_logo from '../../assets/qb_logo.jpg'; 
import survey_logo from '../../assets/survey_logo.png'; 
import schedule_logo from '../../assets/schedule_logo.jpg';

function MainPageInstructor() {
  let navigate = useNavigate();


  let HACKINGTRY = localStorage.getItem("loginID") == null ? true : false;
  if (HACKINGTRY) {
    return <div className={styles.label}> ACCESS DENIED </div>;
  } else {
    // TEMP DEL ACCOUNT BUTTON AFTER MYSURVEY'S BUTTON!
    return (
      <div>
        <div>
          <SideBar />
        </div>
        <div>
          <TopBar />
        </div>
        
  
        <div>
          <div className={styles.ul}>
            <div className={styles.li}>
              <div className={styles.coursesDiv}>
                <img src={myCourses_logo} alt="" width="20%" height="20%"  border-top-left-radius= "10px" border-top-right-radius="10px" />
                <div onClick={() => navigate('/instructorCourses')} className={styles.course}>My Courses</div>
              </div>

              <div className={styles.qbDiv}>
                <img src={qb_logo} alt="" width="20%" height="20%"  />
                <div onClick={() => navigate('/instructorQB')} className={styles.qb}>MyQB</div>
              </div>
            </div>
            <div className={styles.li}>
              <div className={styles.surveysDiv}>
                <img src={survey_logo} alt="" width="20%" height="20%"  />
                <div onClick={() => navigate('/instructorSurveys')} className={styles.surveys}>My Surveys</div>
              </div>

              <div className={styles.scheduleDiv}>
                <img src={schedule_logo} alt="" width="20%" height="20%"  />
                <div onClick={() => navigate('/instructorSchedule')} className={styles.schedule}>Schedule</div>
              </div>
              
            </div>
          </div>
        </div>
        



      </div>
    );
  }
}

export default MainPageInstructor;