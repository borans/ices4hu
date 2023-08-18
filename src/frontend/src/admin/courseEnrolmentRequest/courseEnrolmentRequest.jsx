import React, {useState, useEffect} from "react";
import "./courseEnrolmentStyle.css";
import { useNavigate } from 'react-router-dom';
import CourseEnrolment_Table from "../courseEnrolmentTable/courseEnrolmentTable";
import SideBar from "../sidebarAdmin/sidebarAdmin";
import TopBar from "../topbarAdmin/topbarAdmin";
import my_courses from "../../assets/my_courses.png";
import folder_logo from "../../assets/folder_logo.png";


function CourseEnrolmentRequest() {

    const [requestList, setRequestList] = useState([]);
    const [error, setError] = useState(null);
    const [isLoaded, setIsLoaded] = useState(false);
    const [courseName, setCourseName] = useState("");
    const [courseCode, setCourseCode] = useState("");
    const [department, setDepartment] = useState("");
    const [credit, setCredit] = useState(0);
    const [degree, setDegree] = useState("Undergraduate");
    const [type, setType] = useState("Mandatory");


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
      setDegree(event.target.value);
    };

    const handleTypeChange = (event) => {
      setType(event.target.value);
    };

    const navigate = useNavigate();
    const handleClick = () => {
        navigate('/mainPageAdmin');
    };

    let HACKINGTRY = localStorage.getItem("loginID") == null ? true : false;


    useEffect(() => {
        fetchRequests()
    }, [])


    const handleApproveOrDeny = (newEnrolReq) => {
        console.log(newEnrolReq);

        if(newEnrolReq.enrolment.approveClicked & !newEnrolReq.enrolment.denyClicked){
            sendApproveRequest(newEnrolReq.enrolment.requestId);
        }else if(!newEnrolReq.enrolment.approveClicked & newEnrolReq.enrolment.denyClicked ){
            sendDenyRequest(newEnrolReq.enrolment.requestId);
        }
    };


    const sendApproveRequest = (Id) => {

        fetch("/api/course_enrolment/admin/approve?request=" + String(Id) , {
            method: "POST",
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
                alert("Course enrolment request approved.");
                window.location.reload(false);
            })
            .catch((err) => {
                console.log("Error:", err);
            }); 


    };

    const sendDenyRequest = (Id) => {

        fetch("/api/course_enrolment/admin/deny?request=" + String(Id) , {
            method: "POST",
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
                alert("Course enrolment request denied.");
                window.location.reload(false);
            })
            .catch((err) => {
                console.log("Error:", err);
            }); 



    };





    const fetchRequests = () => {
        fetch("/api/course_enrolment/admin", {
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
                console.log("RESULT!!:", result);
                setRequestList(result);
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
                  <h1 className="page-title">Course Enrolment Requests</h1>

            <div name="tableCourse" >

                <table>

                    <thead>

                    <tr>
                        <th>Course Type</th>
                        <th>Course Code</th>
                        <th>Course Name</th>
                        <th>Student Name</th>
                        <th>Student Surname</th>
                        <th>Student e-mail</th>
                        <th>Request Time</th>
                        <th>Approve Request</th>
                        <th>Deny Request</th>
                    </tr>
                    </thead>
                    <tbody>

                        {requestList.map(request => (
                        <CourseEnrolment_Table key={request.id}
                            courseType = {request.course.courseType}
                            courseCode = {request.course.code}
                            courseName = {request.course.name}
                            studentName = {request.studentName}
                            studentSurname = {request.studentSurname}
                            studentemail = {request.studentEMail}
                            requestTime = {request.requestTime}
                            requestId = {request.requestId}
                            onSave = {handleApproveOrDeny}
                            />
                    ))}

                    </tbody>
                </table>

                <div>
                <button type="submit"  
                onClick={(event) => {event.preventDefault(); navigate(`/viewUserAndAddInstructor`)}}
                >Back</button>

                </div>
                 </div>
               </div>
            </div>
        </div>

    );
    }
}

export default CourseEnrolmentRequest;