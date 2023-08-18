import React, {useState, useEffect} from "react";
import "./userEnrolmentRequest.css";
import { useNavigate } from 'react-router-dom';
import UserEnrolment_Table from "../userEnrolmentTable/userEnrolmentTable";
import SideBar from "../sidebarAdmin/sidebarAdmin";
import TopBar from "../topbarAdmin/topbarAdmin";


function UserEnrolmentRequest() {

    const [requestList, setRequestList] = useState([]);
    const [error, setError] = useState(null);
    const [isLoaded, setIsLoaded] = useState(false);
    const [accountType, setAccountType] = useState("");
    const [name, setName] = useState("");
    const [surname, setSurname] = useState("");
    const [email, setEmail] = useState("");
    const [department, setDepartment] = useState("");
    const [requestTime, setRequestTime] = useState(0);


    const handleAccountTypeChange = (event) => {
      setAccountType(event.target.value);
    };

    const handleNameChange = (event) => {
      setName(event.target.value);
    };

    const handleSurnameChange = (event) => {
      setSurname(event.target.value);
    };

    const handleEmailChange = (event) => {
      setEmail(event.target.value);
    };

    const handleDepartmentChange = (event) => {
      setDepartment(event.target.value);
    };

    const handleRequestTimeChange = (event) => {
      setRequestTime(event.target.value);
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
    }


    const sendApproveRequest = (Id) => {

        fetch("/api/user_enrolment/admin/approve?request=" + String(Id) , {
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
                alert("User enrolment request approved.");
                window.location.reload(false);
            })
            .catch((err) => {
                console.log("Error:", err);
            }); 


    };

    const sendDenyRequest = (Id) => {

        fetch("/api/user_enrolment/admin/deny?request=" + String(Id) , {
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
                alert("User enrolment request denied.");
                window.location.reload(false);
            })
            .catch((err) => {
                console.log("Error:", err);
            }); 



    };

    const fetchRequests = () => {
        fetch("/api/user_enrolment/admin", {
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
                  <h1 className="page-title">User Enrolment Requests</h1>

            <div name="tableCourse" >

                <table>

                    <thead>

                    <tr>
                        <th>Account Type</th>
                        <th>Name</th>
                        <th>Surname</th>
                        <th>Email</th>
                        <th>Department</th>
                        <th>Request Time</th>
                        <th>Approve Request</th>
                        <th>Deny Request</th>
                    </tr>
                    </thead>
                    <tbody>
                        {requestList.map(request => (
                        <UserEnrolment_Table key={request.requestId}
                            accountType = {request.accountType}
                            name = {request.name}
                            surname = {request.surname}
                            email = {request.email}
                            department = {request.department}
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

export default UserEnrolmentRequest;