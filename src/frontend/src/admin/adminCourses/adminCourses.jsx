import React, {useState, useEffect} from "react";
import "./adminCoursesStyle.css";
import { useNavigate } from 'react-router-dom';
import CourseTable_Admin from "../courseTableAdmin/courseAdmin";
import SideBar from "../sidebarAdmin/sidebarAdmin";
import TopBar from "../topbarAdmin/topbarAdmin";
import my_courses from "../../assets/my_courses.png";
import folder_logo from "../../assets/folder_logo.png";


function AdminCourses() {

    const [courseList, setCourseList] = useState([]);
    const [error, setError] = useState(null);
    const [isLoaded, setIsLoaded] = useState(false);
    const [isPopupOpen, setIsPopupOpen] = useState(false);
    const [isEditPopupOpen, setIsEditPopupOpen] = useState(false);
    const [courseName, setCourseName] = useState("");
    const [courseCode, setCourseCode] = useState("");
    const [department, setDepartment] = useState("");
    const [credit, setCredit] = useState(0);
    const [degree, setDegree] = useState(true);
    const [type, setType] = useState(true);
    const [departments, setDepartments] = useState([]);
    const [Id, setId] = useState(0);

    const [editCourseId, setEditCourseId] = useState(0);
    const [editCourseName, setEditCourseName] = useState("");
    const [editCredit, setEditCredit] = useState(0);

  const openPopup = () => {
    setDegree(true);
    setType(true);
    document.getElementById("popup").style.display = "block";
  }

  const openEditPopup = () => {

    document.getElementById("edit-popup").style.display = "block";
  }

  const closePopup = () => {
    document.getElementById("popup").style.display = "none";
    setDegree(true);
    setType(true);
    setCourseName("");
    setCourseCode("");
    setDepartment("");
    setCredit(0);
  }

  const closeEditPopup = () => {
    document.getElementById("edit-popup").style.display = "none";
  }

    const handleEditCourseNameChange = (event) => {
      setEditCourseName(event.target.value);
    }

    const handleEditCreditChange = (event) => {
      setEditCredit(event.target.value);
    };

    const handleCourseNameChange = (event) => {
      setCourseName(event.target.value);
    };

    const handleCourseCodeChange = (event) => {
      setCourseCode(event.target.value);
    };

    const handleDepartmentChange = (event) => {
      setDepartment(event.target.value);
    };

    const handleCreditChange = (event) => {
      setCredit(event.target.value);
    };

    const handleDegreeChange = (event) => {
      event.target.value == "Undergraduate" ? setDegree(true) : setDegree(false);
    };

    const handleTypeChange = (event) => {
      event.target.value == "Mandatory" ? setType(true) : setType(false);
    };


  const handleEdit = (event, courseId) => {
    event.preventDefault();
    console.log("Course Name:", courseName);
    console.log("Course Code:", courseCode);
    console.log("Department:", department);
    console.log("Credit:", credit);
    console.log("Degree:", degree);
    console.log("Type:", type);

    fetch("/api/courses/admin?course=" + editCourseId, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": localStorage.getItem("authToken")
      },
      body: JSON.stringify({
        name:editCourseName,
        credit:editCredit==="" ? null: Number(editCredit),
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
            alert("Operation successful!");
            var newCourse = courseList.find((course) => {return course.id === editCourseId});
            newCourse.name = editCourseName;
            newCourse.credit = editCredit;
            setCourseList((courseList) => courseList.map((course) =>
              course.id === editCourseId ? newCourse : course
            ));
        } else {
            result.json.then((json) => {
                alert(json.message);
            })
        }
      })
      .catch((err) => {
        console.log("Error:", err);
      });

    closeEditPopup();
  };

  const onEditClick = (courseId, courseName, courseCredit) => {
    setEditCourseId(courseId);
    setEditCourseName(courseName);
    setEditCredit(courseCredit);
    openEditPopup();
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    console.log("Course Name:", courseName);
    console.log("Course Code:", courseCode);
    console.log("Department:", department);
    console.log("Credit:", credit);
    console.log("Degree:", degree);
    console.log("Type:", type);

    fetch("/api/courses/admin/create_course", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": localStorage.getItem("authToken")
      },
      body: JSON.stringify({
        name:courseName,
        code:courseCode,
        departmentId:Number(department),
        credit:credit==="" ? null: Number(credit),
        undergraduate:degree,
        mandatory:type
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
            

            var newCourseDepartment = departments.find((departmentObj) => {return departmentObj.id === Number(department)});
            courseList.push({
              name:courseName,
              code:courseCode,
              department:newCourseDepartment.name,
              credit:credit,
              courseDegree:degree ? "Undergraduate": "Graduate",
              courseType:type ? "Mandatory": "Elective"
            });
            setCourseList((courseList) => courseList.map((course) =>
              course
            ));

            window.location.reload(false);
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

    setCourseName("");
    setCourseCode("");
    setDepartment("");
    setCredit(0);
    setDegree(true);
    setType(true);
    closePopup();
  };

    const navigate = useNavigate();
    const handleClick = () => {
        navigate('/mainPageAdmin');
    };

    let HACKINGTRY = localStorage.getItem("loginID") == null ? true : false;


    useEffect(() => {
        fetchCourses()
        getDepartments()
    }, [])


    const handleDelete = (newCourse) => {

      console.log(newCourse.course.id);
      console.log(typeof(newCourse.course.id));

      fetch("/api/courses/admin?course=" + String(newCourse.course.id), {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          "Authorization": localStorage.getItem("authToken")
      },
        body: "",
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
              alert("Operation successful!");
              setCourseList((current) => 
                    current.filter((course) => course.id !== newCourse.course.id))
          } else {
              result.json.then((json) => {
                  alert(json.message);
              })
          }
        })
        .catch((err) => {
          console.log("Error:", err);
        });

    

    };




    const getDepartments = () => {

      fetch("/api/auth/departments", {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
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
          setDepartments(result);
        })
        .catch((err) => {
          console.log("Error:", err);
        });

    } 


    const fetchCourses = () => {
        fetch("/api/courses/admin", {
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
         <div className="container">
            <div>
                <SideBar />
            </div>

            <div className="container">
                  <h1 className="page-title">Courses</h1>
                  <div className="courses-options">
                    <div className="courses-btn">
                      <button className="add-instructor-button" onClick={(event) => {event.preventDefault(); openPopup();}}>Add Course</button>
                      <div id="popup" className="popup">
                        <div className="popup-content">
                          <span className="close-popup" onClick={closePopup}>&#10005;</span>
                          <h2>Add Course</h2>
                <form onSubmit={handleSubmit}>
                      <label class="form-label" htmlFor="courseName">Course Name:</label>
                      <input
                        type="text"
                        id="courseName"
                        name="courseName"
                        value={courseName}
                        onChange={handleCourseNameChange}
                        required
                      />
                      <label class="form-label" htmlFor="courseCode">Course Code:</label>
                      <input
                        type="text"
                        id="courseCode"
                        name="courseCode"
                        value={courseCode}
                        onChange={handleCourseCodeChange}
                        required
                      />
                      <label class="form-label" htmlFor="department">Department:</label>
                      <select name="department" id="department" value={department} onChange={handleDepartmentChange} required>
                      <option value="">Choose a department</option>
                      {departments.map((department) => (
                        <option key={department.id} value={department.id}>
                        {department.name}
                        </option>
                      ))}
                      </select>
                      <label class="form-label" htmlFor="credit">Credit:</label>
                      <input
                        type="number"
                        id="credit"
                        name="credit"
                        value={credit}
                        onChange={handleCreditChange}
                        min="0" max="10"
                        required
                      />
                     <div>
                       <label class="form-label" htmlFor="degree">Degree:</label>
                       <div className="radio-options">
                         <div class="center">
                           <input class="custom-radio"
                             type="radio"
                             id="undergraduate"
                             name="degree"
                             value="Undergraduate"
                             checked={degree === true}
                             onChange={handleDegreeChange}
                             required
                           />
                           <label class="option-select" htmlFor="undergraduate">Undergraduate</label>
                         </div>
                         <div class="center">
                           <input class="custom-radio"
                             type="radio"
                             id="graduate"
                             name="degree"
                             value="Graduate"
                             checked={!degree}
                             onChange={handleDegreeChange}
                             required
                           />
                           <label class="option-select" htmlFor="graduate">Graduate</label>
                         </div>
                       </div>
                     </div>

                     <div>
                       <label class="form-label" htmlFor="type">Type:</label>
                       <div className="radio-options">
                         <div class="center">
                           <input class="custom-radio"
                             type="radio"
                             id="mandatory"
                             name="type"
                             value="Mandatory"
                             checked={type}
                             onChange={handleTypeChange}
                             required
                           />
                           <label class="option-select" htmlFor="mandatory">Mandatory</label>
                         </div>
                         <div class="center">
                           <input class="custom-radio"
                             type="radio"
                             id="elective"
                             name="type"
                             value="Elective"
                             checked={!type}
                             onChange={handleTypeChange}
                             required
                           />
                           <label class="option-select" htmlFor="elective">Elective</label>
                         </div>
                       </div>
                     </div>

                      <button type="submit" className="action-button" onClick={handleSubmit}>
                        Submit
                      </button>
                    </form>
                        </div>
                      </div>

                      
                      <div id="edit-popup" className="popup">
                        <div className="popup-content">
                          <span className="close-popup" onClick={closeEditPopup}>&#10005;</span>
                          <h2>Edit Course</h2>
                <form onSubmit={handleEdit}>
                      <label class="form-label" htmlFor="courseName">Course Name:</label>
                      <input
                        type="text"
                        id="courseName"
                        name="courseName"
                        value={editCourseName}
                        onChange={handleEditCourseNameChange}
                        required
                      />
                      
                      <label class="form-label" htmlFor="credit">Credit:</label>
                      <input
                        type="number"
                        id="credit"
                        name="credit"
                        value={editCredit}
                        onChange={handleEditCreditChange}
                        min="0" max="10"
                        required
                      />


                      <button type="submit" className="action-button" onClick={handleEdit}>
                        Submit
                      </button>
                    </form>
                        </div>
                      </div>


                    </div>

            <div name="tableCourse" >

                <table>

                    <thead>

                    <tr>
                        <th>Course Code</th>
                        <th>Course Name</th>
                        <th>Department</th>
                        <th>Course Type</th>
                        <th>Undergraduate/Graduate</th>
                        <th>Credits</th>
                        <th>Edit Course</th>
                        <th>Remove Course</th>
                    </tr>
                    </thead>
                    <tbody>

                        {courseList.map(course => (
                        <CourseTable_Admin key={course.id}
                            id = {course.id}
                            code = {course.code}
                            name = {course.name}
                            department = {course.department}
                            courseType = {course.courseType}
                            courseDegree = {course.courseDegree}
                            credit = {course.credit}
                            onEdit = {onEditClick}
                            onSave = {handleDelete}
                             />
                    ))}

                    </tbody>
                </table>
                 </div>
               </div>
            </div>
        </div>
    </div>
    );
    }
}

export default AdminCourses;