import React, { useState, useEffect } from "react";
import"./surveyForm.css";
import { useNavigate } from 'react-router-dom';
import SurveyQuestion from "../surveyQuestion/surveyQuestion";
import { useParams, useLocation, json } from "react-router-dom";
import SideBar from "../sidebarStudent/sidebarStudent";
import TopBar from "../topbarStudent/topbarStudent";

function SurveyForm(props) {
  let navigate = useNavigate();
  const [questions, setQuestions] = useState([]);
  const [error, setError] = useState(null);
  const [isLoaded, setIsLoaded] = useState(false);
  const { surveyID } = useParams();
  const state  = useLocation();
  const [trialCount, setTrialCount] = useState(0);

  let HACKINGTRY = localStorage.getItem("loginID") === null ? true : false;

  useEffect(() => {
    fetchQuestions()
  }, [])

  const fetchQuestions = () => {
    fetch("/api/survey/student/view?survey=" + surveyID + "&student=" + localStorage.getItem("loginID"), {
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
        setTrialCount(result.trialCount);
      })
      .catch((err) => {
        setIsLoaded(true);
        console.log("Error:", err);
      });
  };

  const goBack = (event) => {
    event.preventDefault();
    if(trialCount!==2 || (state.state.status === "DONE"))
      navigate('/studentSurveys')
    else
      alert("You have to submit the form.")
  }

  function isFirstEightAnswers() {
    let i = 0;
    const slicedArray = questions.slice(0, 8);
    slicedArray.map((question) =>
      i = (question.answer === null) ? i : i+1
    )
    if (i !==8) {
      alert("FIRST 8 QUESTIONS MUST BE ANSWERED.")
      return false;
    }
    return true;
  }

  function isDone() {
    if (state.state.status === "DONE") {
      alert("FORM IS ALREADY SUBMITTED.")
      return false;
    }
    else {
      if (trialCount >= 3) {
        alert("YOU HAVE NO RIGHTS REMAINING")
        return false;
      }
      return true;
    }
  }

  function isTimeOk() {

    const startingDateTime = state.state.startingDatetime.replace(' ', 'T');
    const startingDate = new Date(Date.parse(startingDateTime));

    const deadline = state.state.deadline.replace(' ', 'T');
    const endDate = new Date(Date.parse(deadline));

    const currentDate = new Date();

    if (startingDate.getTime() >= endDate.getTime()) {
      alert("CHECK TIME FORMAT")
      return false;
    }
    else {
      if (currentDate.getTime() >= endDate.getTime()) {
        alert("DEADLINE IS PASSED")
        return false;
      }
      else
        if (currentDate.getTime() <= startingDate.getTime()) {
          alert("FORM IS NOT OPENED YET")
          return false;
        }
      return true;
    }
  }


    const handleQuestionChange = (updatedQuestion) => {
      setQuestions((questions) =>
      questions.map((question) =>
        question.question.id === updatedQuestion.question.id ? updatedQuestion : question
      ));
    };

    const saveAnswers = (event) => {
      event.preventDefault();
      
      if (!isDone() || !isTimeOk()) {
        return;
      }
      if (trialCount >= 2) {
        alert("YOU HAVE TO SUBMIT");
        return;
      }

      setTrialCount((prevState) => prevState + 1);


    fetch("/api/survey/student/answer?student=" + localStorage.getItem("loginID") + "&survey=" + surveyID, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": localStorage.getItem("authToken")
      },
      body: JSON.stringify({
        answers: questions.map(({ question, answer }) => ({
          questionId: question.id,
          ...(question.multipleChoice ? { multipleChoiceId: answer ? parseInt(answer) : null } : { content: answer })
        }))
      })
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
      }setIsLoaded(true);
    })
    .catch((err) => {
      setIsLoaded(true);
      console.log("Error:", err);
      alert("COULD NOT SAVE ANSWERS")
    });

  };

  const submitForm = (event) => {
    event.preventDefault();

    if (!isDone() || !isTimeOk() || !isFirstEightAnswers()) {
      return;
    }

    setTrialCount((prevState) => prevState + 1);

    fetch("/api/survey/student/submit?student=" + localStorage.getItem("loginID") + "&survey=" + surveyID, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": localStorage.getItem("authToken")
      },
      body: JSON.stringify({
        answers: questions.map(({ question, answer }) => ({
          questionId: question.id,
          ...(question.multipleChoice ? { multipleChoiceId: answer ? parseInt(answer) : null } : { content: answer })
        }))
      })
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
        state.state.status="DONE";
        result.json.then((json) => {
          alert(json.message);
      })
      } else {
          result.json.then((json) => {
              alert(json.message);
          })
      }setIsLoaded(true);
    })
    .catch((err) => {
      setIsLoaded(true);
      console.log("Error:", err);
      alert("COULD NOT SAVE ANSWERS")
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
              onSave={handleQuestionChange}
            />
          ))}
        <div className="button-group">
        <button className="save" onClick={(event) => goBack(event)}>
            Do It Later
          </button>
          <button className="save" onClick={(event) => saveAnswers(event)}>
            Save All
          </button>
          <button className="submit" onClick={(event) => submitForm(event)}>
            Submit
          </button>
        </div>
      </form>
    </div>
  </div>
</div>
    );
          }
}
export default SurveyForm;
