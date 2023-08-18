import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import styles from "./post.module.css";
import SideBar from "../sidebarInstructor/sidebarInstructor";
import TopBar from "../topbarInstructor/topbarInstructor";

function ViewNewsletterPostInstructor() {
  const [title, setTitle] = useState("");
  const [post, setPost] = useState("");
  const { postId } = useParams();


  useEffect(() => {
    fetchNewsletterPost(postId)
  }, [])

  const fetchNewsletterPost = (postId) => {
    fetch("/api/newsletter/view?user=" + localStorage.getItem("loginID") + "&post=" + postId, {
        method: "GET",
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
                setTitle(json.topic)
                setPost(json.content)
            })
            console.log("success!!!")
            
            
        } else {
            result.json.then((json) => {
                alert(json.message);
            })
        }
      })
        .catch((err) => {
          console.log("Error:", err);
          alert("COULD NOT FETCH THE POST")
        });
};

  return (
    <div>
      <TopBar />
      <div>
        <div>
          <SideBar />
        </div>
        <div className={styles.containerPost}>
          <h2>View Post</h2>
          <div className={styles.inputBox}>
            <label className={styles.label}>Title:</label>
            <div className={styles.inputContainer}>
              <input
                type="text"
                value={title}
                className={styles.input}
              />
            </div>
          </div>
          <div className={styles.inputBox}>
            <label className={styles.label}>Post:</label>
            <div className={styles.inputContainer}>
              <textarea
                value={post}
                className={styles.textarea}
                rows={5}
              />
            </div>
          </div>

        </div>
      </div>
    </div>
  );
}

export default ViewNewsletterPostInstructor;
