import { useState, useEffect } from 'react';
import axios from "axios";
import '../style/keypad.css';

export default function useSecureKeypad() {
  const [keypad, setKeypad] = useState([]);
  const [userInput, setUserInput] = useState(new Array(6).fill(null));

  useEffect(() => {
    // 컴포넌트가 처음 마운트될 때만 키패드를 가져옴
    getSecureKeypad();
  }, []); // 빈 배열을 의존성으로 설정하여, 이 효과가 한 번만 실행되도록 보장

  const getSecureKeypad = async () => {
    try {
      const response = await axios.get('/api/keypad');
      const { image, hashList } = response.data;

      const keypadRows = createKeypadLayout(image, hashList);
      setKeypad(keypadRows);
    } catch (error) {
      console.error('Failed to fetch keypad:', error);
    }
  };

  const createKeypadLayout = (image, hashList) => {
    let rows = [];
    let positionIndex = 0;

    for (let i = 0; i < 3; i++) {
      rows.push(hashList.slice(i * 4, i * 4 + 4).map((hash, index) => {
        if (hash) {
          const position = `-${positionIndex * 50}px 0px`;
          const button = {
            image: image,
            hash,
            position: position,
            backgroundSize: '500px 50px'
          };
          positionIndex++;
          return button;
        } else {
          return { hash: null };
        }
      }));
    }
    return rows;
  };

  const onKeyPressed = (row, col) => {
    const updatedUserInput = [...userInput];
    const emptyIndex = updatedUserInput.findIndex(val => val === null);
    if (emptyIndex !== -1 && keypad[row][col].hash) {
      updatedUserInput[emptyIndex] = keypad[row][col].hash;
      setUserInput(updatedUserInput);
    }

    if (emptyIndex === 5) {
      setTimeout(() => {
        alert(`User Input: ${updatedUserInput.join(' ')}`);
        setUserInput(new Array(6).fill(null));
        getSecureKeypad();
      }, 0);
    }
  };

  return {
    states: {
      keypad,
      userInput,
    },
    actions: {
      onKeyPressed,
    }
  };
}

