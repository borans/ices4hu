import React, { useState, useEffect } from "react";
import { useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import SideBar from "../sidebarInstructor/sidebarInstructor";
import TopBar from "../topbarInstructor/topbarInstructor";
import "./surveyCreateStyle.css"
import {
  faArrowUp,
  faArrowDown,
  faTrash,
  faPlus,
} from '@fortawesome/free-solid-svg-icons';

function SurveyCreate() {
  const [questions, setQuestions] = React.useState([{ title: "", choices: [] }]);
  const [QB, setQB] = useState([]);
  const [courses, setCourses] = useState([]);
  const [selectedOption, setSelectedOption] = useState(0);
  const [isCourseSurvey, setIsCourseSurvey] = useState(false);
  const [startDate, setStartDate] = useState();
  const [deadline, setDeadline] = useState();
  let navigate = useNavigate();
  
  const handleCourseChange = (event) => {
    setSelectedOption(event.target.value);
  };

  useEffect(() => {
    fetchCourses();
    fetchQB();
  }, []);

  const reorderArray = (event, originalArray) => {
    const movedItem = originalArray.find((item, index) => index === event.oldIndex);
    const remainingItems = originalArray.filter((item, index) => index !== event.oldIndex);

    const reorderedItems = [
      ...remainingItems.slice(0, event.newIndex),
      movedItem,
      ...remainingItems.slice(event.newIndex)
    ];

    return reorderedItems;
  };

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
    updatedQuestions[index].choices.splice(idx, 1);
    setQuestions(updatedQuestions);
  }

  function saveQuestion(index, newText) {
    const updatedQuestions = [...questions];
    updatedQuestions[index].title = newText;
    setQuestions(updatedQuestions);
  }

  function addQuestion() {
    const newQuestion = { title: '', choices: [] };
    setQuestions((prevQuestions) => [...prevQuestions, newQuestion]);
  }

  function addfromQB(item) {
    const mc = item.isMultipleChoice ? item.multipleChoices.map((x) => (x.content)) : [];
    const newQuestion = { title: item.question, choices: mc };
    setQuestions((prevQuestions) => [...prevQuestions, newQuestion]);
  }

  function createForm() {
    const surveyBody = {
      ...(isCourseSurvey && { courseId: parseInt(selectedOption) }),
      startingDatetime: startDate,
      deadline: deadline,
      questions: questions.map((question) => {
        const { title, choices } = question;
        const isMultipleChoice = choices.length > 0;
        return {
          question: title,
          isMultipleChoice,
          multipleChoices: isMultipleChoice ? choices.map((choice) => ({ content: choice })) : null,
        };
      }),
    };
    const jsonBody = JSON.stringify(surveyBody);
    console.log(jsonBody);

    fetch("/api/survey/instructor?user=" + localStorage.getItem("loginID"), {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": localStorage.getItem("authToken")
      },
      body: jsonBody,
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
          alert("Survey is created.")
          navigate("/instructorSurveys")
        } else {
            result.json.then((json) => {
                alert(json.message);
            })
        }
      })
      .catch((err) => {
        console.log("Error:", err);
      });
  }

  const openPopup = () => {
    document.getElementById("popup").style.display = "block";
  }

  const closePopup = () => {
    document.getElementById("popup").style.display = "none";
  }

  function addChoices(index) {
    const updatedQuestions = [...questions];
    updatedQuestions[index].choices.push('');
    setQuestions(updatedQuestions);
  };

  function saveChoices(index, idx, newText) {
    const updatedQuestions = [...questions];
    updatedQuestions[index].choices[idx] = newText;
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
      })
      .then(result => {
        setQB(result.questions);
      })
      .catch((err) => {
        console.log("Error:", err);
      });
  };

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
      })
      .then(result => {
        setCourses(result);
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
          <h1 className="page-title">Create Survey</h1>
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
                {isCourseSurvey && (
                  <div className="surveyTypeDetail">
                    <label className="optionLabel">Course:</label>
                    <form
                      action="#"
                      className="course-dropdown"
                      method="POST"
                      style={{ display: 'inline-block' }}
                    >
                      <div className="select-box">
                        <div className="options-container">
                          <select
                            className="option"
                            id="NONE"
                            value={selectedOption}
                            onChange={handleCourseChange}
                          >
                            <option value={0}>{"Choose a course"}</option>
                            {courses.map((course) => (
                              <option key={course.id} value={course.id}>
                                {course.name}
                              </option>
                            ))}
                          </select>
                        </div>
                      </div>
                    </form>
                  </div>
                )}
                <div className="surveyTypeDetail">
                  <label className="optionLabel">Start Time:</label>
                  <input
                    type="datetime-local"
                    min={new Date().toISOString().slice(0, -8)}
                    onChange={(e) => setStartDate(e.target.value)}
                  />
                </div>
                <div className="surveyTypeDetail">
                  <label className="optionLabel">End Time:</label>
                  <input
                    type="datetime-local"
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
                      <span className="close-popup" onClick={closePopup}>&#10005;</span>
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
                            value={item.title}
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
                      {item.choices.length > 0 && (
                        
                        <tr>
                          <td colSpan="2">
                            <table>
                              <tbody><div className="choices-row">
                                {item.choices.map((choice, idx) => {
                                  return (
                                    <div key={idx}>

                                    
                                    <tr ><div className="choice-box">
                                      <td> 
                                        <input
                                          type="text"
                                          placeholder="Enter a choice"
                                          value={choice}
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
                <button className="createForm-btn" onClick={(event) => {event.preventDefault(); createForm();}}>Create Form</button>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}

export default SurveyCreate;