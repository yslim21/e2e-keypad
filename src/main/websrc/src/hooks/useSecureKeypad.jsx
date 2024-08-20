import { useState, useEffect } from 'react';
import axios from "axios";
import '../style/keypad.css';
import JSEncrypt from 'jsencrypt';

export default function useSecureKeypad() {
  const [keypad, setKeypad] = useState([]);
  const [userInput, setUserInput] = useState(new Array(6).fill(null));
  const [publicKey, setPublicKey] = useState('');
  const [keypadId, setKeypadId] = useState(''); // id 상태 추가

  useEffect(() => {
    // 컴포넌트가 처음 마운트될 때만 키패드를 가져옴
    getSecureKeypad();
    fetchPublicKey();
  }, []); // 빈 배열을 의존성으로 설정하여, 이 효과가 한 번만 실행되도록 보장

  const getSecureKeypad = async () => {
    try {
      const response = await axios.get('/api/keypad');
      const { image, hashList, id } = response.data; // id를 함께 받음

      const keypadRows = createKeypadLayout(image, hashList);
      setKeypad(keypadRows);
      setKeypadId(id); // id 상태 설정
      //console.log(id)
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

  const fetchPublicKey = async () => {
    try {
      const response = await fetch('/public_key.pem'); // 공개키 파일 경로
      const key = await response.text();
      setPublicKey(key);
    } catch (error) {
      console.error('Failed to fetch public key:', error);
    }
  };

  const encryptData = (data) => {
    const encrypt = new JSEncrypt();
    encrypt.setPublicKey(publicKey); // 공개키 설정
    return encrypt.encrypt(data); // 데이터 암호화
  };

  const onKeyPressed = (row, col) => {
    const updatedUserInput = [...userInput];
    const emptyIndex = updatedUserInput.findIndex(val => val === null);
    if (emptyIndex !== -1 && keypad[row][col].hash) {
      updatedUserInput[emptyIndex] = keypad[row][col].hash;
      setUserInput(updatedUserInput);
    }

    if (emptyIndex === 5) {
      setTimeout(async () => {
        //alert(`User Input: ${updatedUserInput.join(' ')}`);

        const combinedHash = updatedUserInput.join('');
        const encryptedData = encryptData(combinedHash);

        //console.log(encryptedData);

        const response = await axios.post('http://localhost:8080/api/receiveEncryptedData', {
          id: keypadId, // id를 함께 보냄
          encryptedData: encryptedData
        });

        //console.log("Response from server:", response.data);
        alert(response.data);

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

