import React, { useState, useEffect } from "react";
import styles from "../../components/courses.module.css";
import { useNavigate } from 'react-router-dom';
import CourseTable_Manager from "../courseTableManager/courseManager";
import SideBar from "../sidebarManager/sidebarManager";
import TopBar from "../topbarManager/topbarManager";
import my_courses from "../../assets/my_courses.png";
import folder_logo from "../../assets/folder_logo.png";

function ManagerCourses() {
 

  const [courseList, setCourseList] = useState([]);
  const [instructorList, setInstructorList] = useState([]);
  const [error, setError] = useState(null);
  const [isLoaded, setIsLoaded] = useState(false);
  //const [pairs, setPairs] = useState([]);
  const pairs = [];

  const navigate = useNavigate();
  const handleClick = () => {
    navigate('/mainPageManager');
  };

  let HACKINGTRY = localStorage.getItem("loginID") == null ? true : false;

  useEffect(() => {
    fetchCourses()
    fetchInstructors()
  }, [])

  const fetchCourses = () => {
    fetch("/api/courses/department_manager?user=" + localStorage.getItem("loginID"), {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": localStorage.getItem("authToken")
      },
    })
      .then((res) => {
        if (res.ok)
          return res.json();
        else
          throw new Error("AN ISSUE CONNECTING TO BACKEND");
      })
      .then(result => {
        setCourseList(result.courses);
        setIsLoaded(true);
      })
      .catch((err) => {
        setIsLoaded(true);
        setError(err);
      });
  };

  const fetchInstructors = () => {
      fetch("/api/courses/department_manager?user=" + localStorage.getItem("loginID"), {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": localStorage.getItem("authToken")
        },
      })
        .then((res) => {
          if (res.ok)
            return res.json();
          else
            throw new Error("AN ISSUE CONNECTING TO BACKEND");
        })
        .then(result => {
           setInstructorList(result.instructors);
                  setIsLoaded(true);
        })
        .catch((err) => {
          setIsLoaded(true);
          setError(err);
        });
  };

  const handleRequest = (newInstructor) => {

    console.log(newInstructor);

    let index = -1;
     pairs.forEach((pair) => {
        if(pair.courseId === newInstructor.instructor.courseId){
            index = pairs.indexOf(pair);
            pairs.splice(index, 1);
        }

     });
     
      
      pairs.push({courseId: newInstructor.instructor.courseId, instructorId: newInstructor.instructor.instructorId});
      

      //setPairs([...pairs, {courseId: newInstructor.instructor.courseId, instructorId: Number(newInstructor.instructor.instructorId)}]);

      //console.log(pairs);
  }


  const sendRequest = () => {

    fetch("/api/courses/department_manager/assign_instructors?user=" + localStorage.getItem("loginID") , {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": localStorage.getItem("authToken")
      },
      body: JSON.stringify({
        pairs: pairs
        
      }),
    })
    .then((res) => {
      if (res.ok) {
          return {json: res.json(),
              success: true};
      }

      else {
          return {json: res.json(),
              success: false};
      }
    })
    .then((result) => {

      if (result.success) {
        result.json.then((json) => {
          alert(json.message);
      })
      } else {
          result.json.then((json) => {
              alert(json.message);
          })
      }
    })
      .catch((err) => {
        console.log("Error:", err);
        alert("COULD NOT SAVE SCHEDULE")
      });
  };






  if (HACKINGTRY) {
    return <div> ACCESS DENIED </div>;
  } else if (error) {
    return <div> Error !!!</div>;
  } else if (!isLoaded) {
    return <div> Loading... </div>;
  } else {
    return (
      <div>
        <TopBar />
        <div className={styles.page}>
          <div>
            <SideBar />
          </div>
          <div className="container">
            <h1 className="page-title">Department Courses</h1>
            <div name="tableCourse">
              <table className={styles.table}>
                <thead className={styles.th}>
                  <tr className={styles.tr}>
                  <th>Course Id</th>
                    <th>Course Code</th>
                    <th>Course Name</th>
                    <th>Instructor</th>
                    <th>Department</th>
                    <th>Course Type</th>
                    <th>Undergraduate/Graduate</th>
                    <th>Credits</th>
                  </tr>
                </thead>
                <tbody className={styles.td}>
                  {courseList.map(course => (
                    <CourseTable_Manager
                      key={course.id}
                      id={course.id}
                      code={course.code}
                      name={course.name}
                      department={course.department}
                      courseType={course.courseType}
                      courseDegree={course.courseDegree}
                      credit={course.credit}
                      instructors={instructorList}
                      instructorName = {course.instructorName}
                      onSave = {handleRequest}  
                    />
                    
                    
                  ))}
                  
                </tbody>
              </table>
              <button onClick={sendRequest}>SAVE</button>
            </div>
          
          </div>
        </div>
      </div>
    );
  }
}

export default ManagerCourses;