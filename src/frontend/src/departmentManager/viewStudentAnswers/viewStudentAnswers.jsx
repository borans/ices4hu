import React, { useState, useEffect } from "react";
import"./viewStudentAnswers.css";
import SurveyQuestion from "./surveyQuestion";
import { useParams} from "react-router-dom";
import SideBar from "../sidebarManager/sidebarManager";
import TopBar from "../topbarManager/topbarManager";

function ViewStudentAnswers(props) {
  const [questions, setQuestions] = useState([]);
  const [error, setError] = useState(null);
  const [isLoaded, setIsLoaded] = useState(false);
  const {surveyID, studentUserName} = useParams();

  let HACKINGTRY = localStorage.getItem("loginID") === null ? true : false;

  useEffect(() => {
    fetchQuestions()
  }, [])

  const fetchQuestions = () => {
    fetch("/api/survey/department_manager/view_student_answers?survey=" + surveyID + "&student=" + studentUserName + "&departmentManager=" + localStorage.getItem("loginID"), {
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
        setQuestions(result.questions);
        setIsLoaded(true);
      })
      .catch((err) => {
        setIsLoaded(true);
        setError(err);
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
  <div className="survey-form">
    <div className="sidebar">
      <SideBar />
    </div>
    <div className="form-container">
      <form className="form">
        <label>QUESTIONS</label>
        {questions
          .map((question) => (
            <SurveyQuestion
              key={question.question.id}
              id={question.question.id}
              content={question.question.content}
              multipleChoices={question.question.multipleChoices}
              multipleChoice={question.question.multipleChoice}
              answer={question.answer}
            />
          ))}
      </form>
    </div>
  </div>
</div>
    );
          }
}
export default ViewStudentAnswers;
