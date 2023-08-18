import React, { useState } from "react";
import styles from "./post.module.css";
import SideBar from "../sidebarManager/sidebarManager";
import TopBar from "../topbarManager/topbarManager";

function Newsletter() {
  const [title, setTitle] = useState("");
  const [post, setPost] = useState("");

  const handlePostChange = (event) => {
    setPost(event.target.value);
  };

  const handleTitleChange = (event) => {
    setTitle(event.target.value);
  };

  const handlePost = (event) => {
    event.preventDefault();
    if (title.trim() === "") {
      alert("Title cannot be empty!");
    } else if (post.trim() === "") {
      alert("Post content cannot be empty!");
    } else {

      fetch("/api/newsletter/department_manager?user=" + localStorage.getItem("loginID"), {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": localStorage.getItem("authToken")
        },
        body: JSON.stringify({
          topic: title,
          content: post,
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
          alert("COULD NOT SUBMIT POST")
        });

      setTitle("");
      setPost("");
    }
  };

  return (
    <div>
      <TopBar />
      <div>
        <div>
          <SideBar />
        </div>
        <div className={styles.containerPost}>
          <h2>Create Post</h2>
          <div className={styles.inputBox}>
            <label className={styles.label}>Title:</label>
            <div className={styles.inputContainer}>
              <input
                type="text"
                value={title}
                onChange={handleTitleChange}
                className={styles.input}
              />
            </div>
          </div>
          <div className={styles.inputBox}>
            <label className={styles.label}>Post:</label>
            <div className={styles.inputContainer}>
              <textarea
                value={post}
                onChange={handlePostChange}
                className={styles.textarea}
                rows={5}
              />
            </div>
          </div>

          <button
            className={styles.actionButton}
            onClick={(event) => handlePost(event)}
            disabled={title.trim() === "" || post.trim() === ""}
          >
            Post
          </button>
        </div>
      </div>
    </div>
  );
}

export default Newsletter;
