import React, { useState, useEffect } from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import SideBar from "../sidebarAdmin/sidebarAdmin";
import TopBar from "../topbarAdmin/topbarAdmin";

import { useParams, useLocation } from "react-router-dom";
import {
  faArrowUp,
  faArrowDown,
  faTrash,
  faPlus,
} from '@fortawesome/free-solid-svg-icons';

function SurveyEditAdmin() {

  const { surveyID } = useParams();
  const [questions, setQuestions] = useState([]);
  const { state } = useLocation();
  const [isCourseSurvey, setIsCourseSurvey] = useState();
  const [startDate, setStartDate] = useState();
  const [deadline, setDeadline] = useState();

  useEffect(() => {
    fetchQuestions();
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
    setQuestions(reorderArray({ oldIndex: index, newIndex: index + (direction === "UP" ? (-1) : 1) }, questions));
  }

  function removeQuestion(index) {
    setQuestions((current) =>
      current.filter((question, id) => id !== index)
    );
  }

  function removeChoice(index, idx) {
    const updatedQuestions = [...questions];
    if (updatedQuestions[index].multipleChoices.length > 1)
      updatedQuestions[index].multipleChoices.splice(idx, 1);
    else
      updatedQuestions[index].multipleChoices = null;
    setQuestions(updatedQuestions);
  };


  function saveQuestion(index, newText) {
    const updatedQuestions = [...questions];
    updatedQuestions[index].question = newText;
    setQuestions(updatedQuestions);
  }


  function createFetchBody() {
    const surveyBody = {
      ...(isCourseSurvey && { courseId: parseInt(state.props.courseID) }),
      startingDatetime: startDate,
      deadline: deadline,
      questions: questions.map((x) => {
        if(x.hasOwnProperty('id')){
          x.isMultipleChoice = x.multipleChoices !== null;
          return x;
        }
        else {
          const { question, multipleChoices } = x;
          const isMultipleChoice = multipleChoices !== null;
          return {
            question: question,
            isMultipleChoice,
            ...(isMultipleChoice && {multipleChoices:  multipleChoices}),
          }
        };
      }),
    };

    const jsonBody = JSON.stringify(surveyBody);
    return jsonBody;
  }


  function saveForm() {

    fetch("/api/survey/admin/save?survey=" + surveyID, {
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
      alert("COULD NOT EDIT FORM")
    });
  }



  function addChoices(index) {
    const updatedQuestions = [...questions];
    if (updatedQuestions[index].multipleChoices === null)
      updatedQuestions[index].multipleChoices = []
    updatedQuestions[index].multipleChoices.push({ content: '' });
    setQuestions(updatedQuestions);
  };

  function saveChoices(index, idx, newText) {
    const updatedQuestions = [...questions];
    updatedQuestions[index].multipleChoices[idx].content = newText;
    setQuestions(updatedQuestions);
  }

  const fetchQuestions = () => {
    fetch("/api/survey/admin/view?survey=" + surveyID, {
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
        setDeadline(state.props.deadline);
        setStartDate(state.props.startingDatetime);
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
                <div className="questions">
                  {questions.map((item, index) => {
                    return (

                      <div className="question-body" key={index}>
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
                <button className="createForm-btn" onClick={(event) => {event.preventDefault(); saveForm();}}>Save All</button>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}

export default SurveyEditAdmin; 