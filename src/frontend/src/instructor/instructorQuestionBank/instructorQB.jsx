import React, { useState, useEffect } from "react";
import"./instructorQB.css";
import {useParams, useLocation, json, useNavigate} from "react-router-dom";
import SideBar from "../sidebarInstructor/sidebarInstructor";
import TopBar from "../topbarInstructor/topbarInstructor";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faArrowDown, faArrowUp, faPlus, faTrash} from "@fortawesome/free-solid-svg-icons";

function InstructorQuestionBank(props) {

    const { surveyID } = useParams();
    const [questions, setQuestions] = useState([]);
    const { state } = useLocation();
    const [QB, setQB] = useState([]);
    let questionID;

    useEffect(() => {
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

    function addQuestion(){
        const newQuestion = { question: '', multipleChoices: null };
        setQuestions((prevQuestions) => [...prevQuestions, newQuestion]);
    }
    function addMultipleChoiceQuestion(){
        const newQuestion = { question: '', multipleChoices: [
                {
                    "content": "I strongly disagree"
                },
                {
                    "content": "I disagree"
                },
                {
                    "content": "Not sure"
                },
                {
                    "content": "I agree"
                },
                {
                    "content": "I strongly agree"
                }
            ] };
        setQuestions((prevQuestions) => [...prevQuestions, newQuestion]);
    }
    function addTrueFalseQuestion(){
        const newQuestion = { question: '', multipleChoices: [
                {

                    "content": "True"
                },
                {

                    "content": "False"
                }
            ] };
        setQuestions((prevQuestions) => [...prevQuestions, newQuestion]);
    }


    function createFetchBody(){
        const surveyBody = {
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

    function submitQuestions(){
        console.log(createFetchBody());
        fetch("/api/question_bank/instructor/save?user="+ localStorage.getItem("loginID"), {
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
              alert("COULD NOT SAVE")
            });
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
                console.log(result);
                setQuestions(result.questions);
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
                    <h1 className="page-title">My Question Bank</h1>
                    <div className="createBox">
                        <table className="addQuestionTable">
                            <tbody>
                            <tr>
                                <td>
                                    <div className="addQuestion-btn">
                                        <button onClick={() => addQuestion()}>Add Classic Question</button>
                                        <button onClick={() => addMultipleChoiceQuestion()}>Add Multiple Choice Question</button>
                                        <button onClick={() => addTrueFalseQuestion()}>Add True/False Question</button>
                                    </div>

                                </td>
                            </tr>

                            <div className="questions">
                                {
                                    questions.map((item, index) => {
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
                                                            value={item.question === null ? "Enter a Question" : item.question}
                                                            onChange={(e) => saveQuestion(index, e.target.value)}
                                                        />
                                                    </td>
                                                    <td>
                                                        <div className="questionItems">
                                                            <button onClick={() => changeOrder(index, "UP")}>
                                                                <FontAwesomeIcon icon={faArrowUp} />
                                                            </button>
                                                            <button onClick={() => changeOrder(index, "DOWN")}>
                                                                <FontAwesomeIcon icon={faArrowDown} />
                                                            </button>
                                                            <button onClick={() => addChoices(index)}>
                                                                <FontAwesomeIcon icon={faPlus} /> Choices
                                                            </button>
                                                            <button onClick={() => removeQuestion(index)}>
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
                                                                                        <button onClick={() => removeChoice(index, idx)}>
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
                            <button className="createForm-btn" onClick={() => {
                                submitQuestions();
                            }}>Save All</button>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
}
export default InstructorQuestionBank;
