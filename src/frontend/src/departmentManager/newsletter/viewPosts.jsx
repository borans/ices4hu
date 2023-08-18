import React, {useState, useEffect} from "react";
import "./postStyle.css";
import { useNavigate } from 'react-router-dom';
import NewsletterTable_DM from "../newsletterTable/newsletterTable";
import TopBar from "../topbarManager/topbarManager";
import SideBar from "../sidebarManager/sidebarManager";
import my_courses from "../../assets/my_courses.png";
import folder_logo from "../../assets/folder_logo.png";


function ViewPost() {

    const [postList, setPostList] = useState([]);
    const [error, setError] = useState(null);
    const [isLoaded, setIsLoaded] = useState(false);

    const handlePostListChange = (event) => {
      setPostList(event.target.value);
    };

  const handleSubmit = (event) => {
    event.preventDefault();
  };

    const navigate = useNavigate();
    const handleClick = () => {
        navigate('/mainPageManager');
    };

    let HACKINGTRY = localStorage.getItem("loginID") == null ? true : false;


    useEffect(() => {
        fetchPost()
    }, [])

    const removePost = (event, postId) => {
        event.preventDefault();
        fetch("/api/newsletter/department_manager?user=" + localStorage.getItem("loginID") + "&post=" + postId, {
            method: "DELETE",
            headers: {
              "Content-Type": "application/json",
              "Authorization": localStorage.getItem("authToken")
            },
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
                setPostList((current) => 
                    current.filter((post) => post.id !== postId))
            } else {
                result.json.then((json) => {
                    alert(json.message);
                })
            }

            
          })
            .catch((err) => {
              console.log("Error:", err);
              alert("COULD NOT DELETE THE POST")
            });
    };

    const fetchPost = () => {
        fetch("/api/newsletter?user=" + localStorage.getItem("loginID"), {
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
                setPostList(result);
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

                  <h1 className="page-title">Department Newsletter</h1>
                   <div className="courses-options">
                      <div className="courses-btn">
                            <button onClick={(event) => {event.preventDefault(); navigate('/createPost');}}> Create Newsletter Post </button>
                      </div>
            <div name="tableCourse" >

                <table>

                    <thead>

                    <tr>
                        <th>Post Title</th>
                        <th>Creation Time</th>
                        <th>Edit</th>
                        <th>Delete</th>
                    </tr>
                    </thead>
                    <tbody>

                        {postList.map(post => (
                        <NewsletterTable_DM key={post.id}
                            title = {post.topic}
                            creationDatetime = {post.creationDatetime}
                            postId = {post.id}
                            onDelete = {removePost}
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

export default ViewPost;