import '../style/keypad.css';

export default function KeypadUserInput({ userInput }) {
    return (
        <div className="input-group-style">
            {userInput.map((char, index) => (
                <div key={index} className={`input-char-style ${char ? 'active' : ''}`}></div>
            ))}
        </div>
    );
}