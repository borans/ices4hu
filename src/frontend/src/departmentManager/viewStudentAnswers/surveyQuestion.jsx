import React, { useState } from "react";
import"./surveyQuestion.css";

function SurveyQuestion(props) {
  const { id, content, multipleChoices, multipleChoice, answer} = props;
  const [text, setText] = useState(answer ? answer : "");
  const [radioChoice, setRadioChoice] = useState(answer ? parseInt(answer) : null);


  return (
    <div className="survey-question">
      <div><label>{content}</label>
        </div>
      
      {multipleChoice ? (
        multipleChoices
        .sort((a, b) => a.id - b.id)
          .map((choice) => (
            <label key={choice.id}>
              <input
                type="radio"
                disabled ={true}
                defaultChecked={choice.id === radioChoice}
              />
              {choice.content}
            </label>
          ))
      ) : (
        <input
          type="text"
          value={text}
          readOnly = {true}
          placeholder="(Not Answered)"
        />
      )}
    </div>
  ); 
}

export default SurveyQuestion;