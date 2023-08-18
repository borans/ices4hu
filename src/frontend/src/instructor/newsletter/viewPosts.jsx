import React, {useState, useEffect} from "react";
import "./newsletter.css";
import { useNavigate } from 'react-router-dom';
import NewsletterTable_Instructor from "../newsletterTable/newsletterTable";
import SideBar from "../sidebarInstructor/sidebarInstructor";
import TopBar from "../topbarInstructor/topbarInstructor";


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
        navigate('/mainPageInstructor');
    };

    let HACKINGTRY = localStorage.getItem("loginID") == null ? true : false;


    useEffect(() => {
        fetchPost()
    }, [])

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

            <div name="tableCourse" >

                <table>

                    <thead>

                    <tr>
                        <th>Post Title</th>
                        <th>Creation Time</th>
                        <th>See Post</th>
                    </tr>
                    </thead>
                    <tbody>

                    {postList.map(post => (
                        <NewsletterTable_Instructor key={post.id}
                            postId = {post.id}
                            creationDatetime = {post.creationDatetime}
                            title = {post.topic} />
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

export default ViewPost;