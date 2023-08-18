import React, { useEffect, useState} from 'react';
import './courseRegStyle.css';
import TopBar from "../topbarStudent/topbarStudent";
import SideBar from "../sidebarStudent/sidebarStudent";
import { useNavigate } from 'react-router-dom';

function CourseRegistration() {

  const [courses, setCourses] = useState([]);
  const [courseList, setCourseList] = useState([]);
  const [error, setError] = useState(null);
  const [isLoaded, setIsLoaded] = useState(false);
  const [id, setId] = useState([]); 
  const navigate = useNavigate();



  useEffect(() => {
    const rows = document.querySelectorAll('.course-table tbody tr');

    rows.forEach(function(row) {
      const checkbox = row.querySelector('input[type="checkbox"]');

      checkbox.addEventListener('change', function() {
        if (this.checked) {
          row.classList.add('selected-row');
        } else {
          row.classList.remove('selected-row');
        }
      });
    });
  })

  useEffect(() => {
    fetchCourses()
  }, [])

  let HACKINGTRY = localStorage.getItem("loginID") == null ? true : false;


  const handleChange = (e) => {


    if(e.target.checked){
      setCourses([...courses, {id: Number(e.target.value)}]);
    }else{
      setCourses(courses.filter( (course) => course.id !== Number(e.target.value)),);
    }
  }

  const handleRequest = () => { 
    console.log(courses)

    sendRequest();
  }



    const sendRequest = () => {

      fetch("api/course_registration/student/enrolment_request?user=" + localStorage.getItem("loginID"), {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": localStorage.getItem("authToken")
        },
        body: JSON.stringify({
          courses
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
              navigate("/mainPageStudent");
              alert("Operation successful!");
          } else {
              result.json.then((json) => {
                  alert(json.message);
              })
          }
        })
        .catch((err) => {
          console.log("Error:", err);
        });
    }

    const fetchCourses = () => {
        fetch("/api/course_registration/student?user=" + localStorage.getItem("loginID"), {
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
            }
            )
            .then(result => {
                setCourseList(result);
                setIsLoaded(true);
            })
            .catch((err) => {
                setIsLoaded(true);
                console.log("Error:", err);
            });

    };


    if (HACKINGTRY) {
        return <div> ACCESS DENIED </div>;
    }
    else if (error) {
        return <div> Error !!!</div>;
    } else if (!isLoaded) {
        return <div> Loading... </div>;
    } else {
      
      return (

        <div>
          <TopBar />

        <div className="withside">
          <div>
              <SideBar />
          </div>


        <div className="container">
          <h1 className="page-title">Course Registration</h1>
      
          <table className="course-table">
            <thead>
              <tr>
                <th>Select</th>
                <th>Course Code</th>
                <th>Course Name</th>
                <th>Instructor</th>
                <th>Credits</th>
                <th>Course Types</th>
                <th>Undergraduate/Graduate</th>
              </tr>
            </thead>
            <tbody>
              {courseList.map(course => (
                <tr>
                  <td><input type="checkbox" value={course.id} onChange={(e) => handleChange(e)} /></td>
                  <td>{course.code}</td>
                  <td>{course.name}</td>
                  <td>{course.instructorName}</td>
                  <td>{course.credit}</td>
                  <td>{course.courseType}</td>
                  <td>{course.courseDegree}</td>
                </tr>
              ))}      
            </tbody>
          </table>
      
          <button className="request-enrolment-button" onClick={(event) => {event.preventDefault(); handleRequest();}}>Request Enrolment</button>
            </div>
          </div>


        </div>



      );
  }
}

export default CourseRegistration;
