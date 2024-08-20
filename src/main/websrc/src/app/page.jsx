"use client";

import React, {useEffect} from 'react';
import useSecureKeypad from '../hooks/useSecureKeypad';
import SecureKeypad from "../components/SecureKeypad";
import KeypadUserInput from "../components/KeypadUserInput.jsx";

export default function Page() {
  const { states, actions } = useSecureKeypad();

  useEffect(() => {}, []);

  if (!states.keypad || states.keypad.length === 0) {
    return (
      <div>
        ...isLoading...
      </div>
    )
  } else {
    return (
      <div>
        <KeypadUserInput userInput={states.userInput}/>
        <SecureKeypad keypad={states.keypad} onKeyPressed={actions.onKeyPressed}/>
      </div>
    );
  }
}
