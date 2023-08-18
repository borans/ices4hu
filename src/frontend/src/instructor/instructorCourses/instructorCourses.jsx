import React, {useState, useEffect} from "react";
import styles from "../../components/courses.module.css";
import { useNavigate } from 'react-router-dom';
import CourseTable_Instructor from "../courseTableInstructor/courseInstructor";
import SideBar from "../sidebarInstructor/sidebarInstructor"
import TopBar from "../topbarInstructor/topbarInstructor";
import my_courses from "../../assets/my_courses.png";
import folder_logo from "../../assets/folder_logo.png";


function InstructorCourses() {

    const [courseList, setCourseList] = useState([]);
    const [error, setError] = useState(null);
    const [isLoaded, setIsLoaded] = useState(false);


    const navigate = useNavigate();
    const handleClick = () => {
        navigate('/mainPageInstructor');
    };

    let HACKINGTRY = localStorage.getItem("loginID") == null ? true : false;


    useEffect(() => {
        fetchCourses()
    }, [])

    const fetchCourses = () => {
        fetch("/api/courses/instructor?user=" + localStorage.getItem("loginID"), {
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
        <div className={styles.page}>
            <div>
                <SideBar />
            </div>

            <div className="container">

                <h1 className="page-title">My Courses</h1>

                <div name="tableCourse" >

                    <table className={styles.table}>

                        <thead className={styles.th}>

                        <tr className={styles.tr}>
                            <th>Course Code</th>
                            <th>Course Name</th>
                            <th>Department</th>
                            <th>Course Type</th>
                            <th>Undergraduate/Graduate</th>
                            <th>Credits</th>
                        </tr>
                        </thead>
                        <tbody className={styles.td}>

                            {courseList.map(course => (
                            <CourseTable_Instructor key={course.id}
                                code = {course.code}
                                name = {course.name}
                                department = {course.department}
                                courseType = {course.courseType}
                                courseDegree = {course.courseDegree}
                                credit = {course.credit} />
                        ))}

                        </tbody>
                    </table>
                </div>
            </div>

        </div>
        </div>
    );
    }
}

export default InstructorCourses;