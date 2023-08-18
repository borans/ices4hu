import React, { useState } from "react";
import"./surveyQuestion.css";

function SurveyQuestion(props) {
  const { id, content, multipleChoices, multipleChoice, answer, onSave } = props;
  const [text, setText] = useState(answer ? answer : "");
  const [radioChoice, setRadioChoice] = useState(answer ? parseInt(answer) : null);


  const handleTextAnswer = (e) => {
    const newText = e.target.value;
    setText(newText);
    onSave(JSON.parse(JSON.stringify({
      question: {
        id: id,
        content: content,
        multipleChoices: multipleChoices,
        multipleChoice: multipleChoice
      },
      answer: newText==="" ? null : newText
    })));
  };

  const handleChoiceAnswer = (idd) => {
    const newChoice = idd === radioChoice ? null : idd;
    setRadioChoice(parseInt(newChoice));
    onSave(JSON.parse(JSON.stringify({
      question: {
        id: id,
        content: content,
        multipleChoices: multipleChoices,
        multipleChoice: multipleChoice
      },
      answer: newChoice ? newChoice : null
    })));
  };

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
                value={choice.content}
                checked={choice.id === radioChoice}
                onClick={() => handleChoiceAnswer(choice.id)}
              />
              {choice.content}
            </label>
          ))
      ) : (
        <input
          type="text"
          value={text}
          onChange={handleTextAnswer}
          placeholder="Give an answer..."
        />
      )}
    </div>
  ); 
}

export default SurveyQuestion;