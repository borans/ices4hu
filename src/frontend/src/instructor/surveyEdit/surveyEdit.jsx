import React, { useState, useEffect } from "react";
import { useNavigate } from 'react-router-dom';
import { useParams, useLocation, json } from "react-router-dom";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import SideBar from "../sidebarInstructor/sidebarInstructor";
import TopBar from "../topbarInstructor/topbarInstructor";
import {
  faArrowUp,
  faArrowDown,
  faTrash,
  faPlus,
} from '@fortawesome/free-solid-svg-icons';

function SurveyEdit(props) {
  let navigate = useNavigate();
  const { surveyID } = useParams();
  const [questions, setQuestions] = useState([]);
  const [QB, setQB] = useState([]);
  const [isCourseSurvey, setIsCourseSurvey] = useState();
  const state = useLocation();
  const [startDate, setStartDate] = useState();
  const [deadline, setDeadline] = useState();
  const [trialCount, setTrialCount] = useState(0);

  useEffect(() => {
        fetchQuestions();
        fetchQB();

  }, [])

  const reorderArray = (event, originalArray) => {
    const movedItem = originalArray.find((item, index) => index === event.oldIndex);
    const remainingItems = originalArray.filter((item, index) => index !== event.oldIndex);

    const reorderedItems = [
      ...remainingItems.slice(0, event.newIndex),
      movedItem,
      ...remainingItems.slice(event.newIndex)
    ];

    return reorderedItems;
  }

  function changeOrder(index, direction) {
    setQuestions(reorderArray({oldIndex: index, newIndex: index + (direction === "UP" ? (-1) : 1)}, questions));
  }

  function removeQuestion(index) {
    setQuestions((current) =>
    current.filter((question, id) => id !== index)
  );
  }

  function removeChoice(index, idx) {
  const updatedQuestions = [...questions];
  if(updatedQuestions[index].multipleChoices.length>1)
  updatedQuestions[index].multipleChoices.splice(idx, 1);
  else
  updatedQuestions[index].multipleChoices = null;
  setQuestions(updatedQuestions);
};
  

  function saveQuestion(index, newText){
    const updatedQuestions = [...questions];
    updatedQuestions[index].question = newText;
    setQuestions(updatedQuestions);
  }

  function extendDeadline(){
    if(state.state.status !== "SUBMITTED")
      return;
    fetch("/api/survey/instructor/extend?survey="+surveyID+"&instructor=" + localStorage.getItem("loginID"), {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": localStorage.getItem("authToken")
      },
      body: JSON.stringify({
        deadline: deadline,
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
        state.state.status = "SUBMITTED";
        alert("Deadline has been extended.")
      } else {
          result.json.then((json) => {
              alert(json.message);
          })
      }
    })
    .catch((err) => {
      console.log("Error:", err);
      alert("COULD NOT SUBMIT FORM")
    });
  }


    function goBack (){
    if(trialCount!==3 || (state.state.status === "SUBMITTED"))
      navigate('/instructorSurveys')
    else
      alert("You have to submit the form.")
  }


    function addQuestion(){
    const newQuestion = { question: '', multipleChoices: null };
    setQuestions((prevQuestions) => [...prevQuestions, newQuestion]);
  }

  function addfromQB(item){
    const mc = item.isMultipleChoice ? item.multipleChoices.map((x) => ({content:  x.content})) : null; 
    const newQuestion = { question: item.question, multipleChoices: mc };
    setQuestions((prevQuestions) => [...prevQuestions, newQuestion]);
  }

  function createFetchBody(){
    const surveyBody = {
      ...(isCourseSurvey && { courseId: parseInt(state.state.courseID) }),
      startingDatetime: startDate,
      deadline: deadline,
      questions: questions.map((x) => {
        if(x.hasOwnProperty('id')){
          x.isMultipleChoice = x.multipleChoices !== null;
          return x;
        }
        else{
        const { question, multipleChoices } = x;
        const isMultipleChoice = multipleChoices !== null;
        return {
          question: question,
          isMultipleChoice,
          ...(isMultipleChoice && {multipleChoices:  multipleChoices}),
        }};
      }),
    };
  
    const jsonBody = JSON.stringify(surveyBody);

    return jsonBody;
  }

    function submitForm() {
    if(state.state.status === "SUBMITTED"){
      extendDeadline();
    }
    else{
    fetch("/api/survey/instructor/submit?survey="+surveyID+"&instructor=" + localStorage.getItem("loginID"), {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": localStorage.getItem("authToken")
      },
      body: createFetchBody(),
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
        state.state.status = "SUBMITTED";
        alert("Survey has been submitted.")
      } else {
          result.json.then((json) => {
              alert(json.message);
          })
      }
    })
    .catch((err) => {
      console.log("Error:", err);
      alert("COULD NOT SUBMIT FORM")
    });
  }
  }

    function saveForm (){
  fetch("/api/survey/instructor/save?survey="+surveyID+"&instructor=" + localStorage.getItem("loginID"), {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": localStorage.getItem("authToken")
    },
    body: createFetchBody(),
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
      setTrialCount(trialCount+1);
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
    alert("COULD NOT SAVE ANSWERS")
  });
  }

  const openPopup = () => {
    document.getElementById("popup").style.display = "block";
  }

  const closePopup = () => {
    document.getElementById("popup").style.display = "none";
  }

  function addChoices(index){
    const updatedQuestions = [...questions];
    if(updatedQuestions[index].multipleChoices === null)
        updatedQuestions[index].multipleChoices = []
    updatedQuestions[index].multipleChoices.push({content: ''});
    setQuestions(updatedQuestions);
  };

  function saveChoices(index, idx, newText){
    const updatedQuestions = [...questions];
    updatedQuestions[index].multipleChoices[idx].content = newText;
    setQuestions(updatedQuestions);
  }

  const fetchQB = () => {
    closePopup();
    fetch("/api/question_bank/instructor?user=" + localStorage.getItem("loginID"), {
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
        setQB(result.questions);
      })
      .catch((err) => {
        console.log("Error:", err);
      });
  };


  const fetchQuestions = () => {
    fetch("/api/survey/instructor/view?survey=" + surveyID + "&instructor=" + localStorage.getItem("loginID"), {
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
          setQuestions(result.questions)
          setTrialCount(result.trialCount)
          setDeadline(state.state.deadline);
          setStartDate(state.state.startingDatetime);
          setIsCourseSurvey(state.state.surveyType==="Course")
        })
        .catch((err) => {
          console.log("Error:", err);
        });
  };


  return (
    <div>
      <TopBar />
      <div>
        <div>
          <SideBar />
        </div>
        <div className="container">
          <h1 className="page-title">Edit Survey</h1>
          <div className="createBox">
            <div className="createOptions">

            <div className="surveyTypeSelection">
                <label className="optionLabel">Survey Type:</label>
                <div className="surveyTypeOption">
                  <label>
                    <input
                      type="radio"
                      className="radioButton"
                      value="Instructor"
                      checked={!isCourseSurvey}
                      onChange={() => setIsCourseSurvey(false)}
                    />
                    Instructor
                  </label>
                </div>
                <div className="surveyTypeOption">
                  <label>
                    <input
                      type="radio"
                      className="radioButton"
                      value="Course"
                      checked={isCourseSurvey}
                      onChange={() => setIsCourseSurvey(true)}
                    />
                    Course
                  </label>
                </div>
              </div>

              <div className="surveyTypeDetails">
                <div className="surveyTypeDetail">
                  <label className="optionLabel">Start Time:</label>
                  <input
                    type="datetime-local"
                    value={startDate}
                    min={new Date().toISOString().slice(0, -8)}
                    onChange={(e) => setStartDate(e.target.value)}
                  />
                </div>
                <div className="surveyTypeDetail">
                  <label className="optionLabel">End Time:</label>
                  <input
                    type="datetime-local"
                    value={deadline}
                    min={new Date().toISOString().slice(0, -8)}
                    onChange={(e) => setDeadline(e.target.value)}
                  />
                </div>
              </div>
            </div>
            <table className="addQuestionTable">
              <tbody>
                <tr>
                  <td>
                  <div className="addQuestion-btn">
                    <button onClick={(event) => {event.preventDefault(); addQuestion();}}>Add Question</button>
                    <button onClick={(event) => {event.preventDefault(); openPopup();}}>Add Question from QB</button>
                    </div>
                    <div id="popup" className="popup">
                      <div className="popup-content">
                      <h2>Add Question from QB</h2>
                      <span className="close-popup" onClick={(event) => {event.preventDefault(); closePopup();}}>&#10005;</span>
                      <div>
                        {QB.map((item, index) => {
                          return (
                            
                            <tbody key={index}>
                              <tr>
                                <td>{index + 1}</td>
                                <td>{item.question}</td>
                                <div>         
                                  <button onClick={(event) => {event.preventDefault(); addfromQB(item);}}>
                                    <FontAwesomeIcon icon={faPlus} />
                                  </button>
                                </div>
                              </tr>
                            </tbody>
                          );
                        })}</div>
                      </div>
                    </div>
                  </td>
                </tr>

                <div className="questions">
                {questions.map((item, index) => {
                  return (

                    <div key={index} className="question-body"> 
                    <div className="question-row">
                    <tbody >
                      <tr>
                        <td>{index + 1}</td>
                        <td>
                          <input
                            type="text"
                            placeholder="Enter a question"
                            value={item.question}
                            onChange={(e) => saveQuestion(index, e.target.value)}
                          />
                        </td>
                        <td>
                          <div className="questionItems"> 
                            <button onClick={(event) => {event.preventDefault(); changeOrder(index, "UP");}}>
                              <FontAwesomeIcon icon={faArrowUp} />
                            </button>
                            <button onClick={(event) => {event.preventDefault(); changeOrder(index, "DOWN");}}>
                              <FontAwesomeIcon icon={faArrowDown} />
                            </button>
                            <button onClick={(event) => {event.preventDefault(); addChoices(index);}}>
                              <FontAwesomeIcon icon={faPlus} /> Choices
                            </button>
                            <button onClick={(event) => {event.preventDefault(); removeQuestion(index);}}>
                              <FontAwesomeIcon icon={faTrash} />
                            </button>
                          </div>
                        </td>
                      </tr>
                      {item.multipleChoices !== null && (
                        
                        <tr>
                          <td colSpan="2">
                            <table>
                              <tbody><div className="choices-row">
                                {item.multipleChoices.map((choice, idx) => {
                                  return (
                                    <div key={idx}>


                                    <tr ><div className="choice-box">
                                      <td> 
                                        <input
                                          type="text"
                                          placeholder="Enter a choice"
                                          value={choice.content}
                                          onChange={(e) => saveChoices(index, idx, e.target.value)}
                                        />
                                      </td>
                                      <td>
                                        <button onClick={(event) => {event.preventDefault(); removeChoice(index, idx);}}>
                                          <FontAwesomeIcon icon={faTrash} />
                                        </button>
                                      </td> </div>
                                    </tr>
                                    </div>
                                  );
                                })}</div>
                              </tbody> 
                            </table> 
                          </td>
                        </tr>
                      )}
                    </tbody> </div>
                    </div>
                  );
                })}
                </div>
                <button className="createForm-btn" onClick={(event) => {event.preventDefault();goBack()}}>Do It later</button>
                <button className="createForm-btn" onClick={(event) => {event.preventDefault();saveForm()}}>Save Form</button>
                <button className="createForm-btn" onClick={(event) => {event.preventDefault();submitForm()}}>Submit Form</button>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}

export default SurveyEdit; 