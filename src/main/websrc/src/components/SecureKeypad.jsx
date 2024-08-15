import '../style/keypad.css';

export default function SecureKeypad({ keypad, onKeyPressed }) {
    return (
        <table className="table-style">
            <tbody>
            {keypad.map((row, rowIndex) => (
                <tr key={rowIndex}>
                    {row.map((button, colIndex) => (
                        <td key={colIndex} className="td-style">
                            {button.hash ? (
                                <button
                                    className="button-style"
                                    onClick={() => onKeyPressed(rowIndex, colIndex)}
                                    style={{
                                        backgroundImage: `url(data:image/png;base64,${button.image})`,
                                        backgroundPosition: button.position,
                                        backgroundSize: button.backgroundSize,
                                        backgroundRepeat: 'no-repeat',
                                        border: 'none',
                                        width: '50px',
                                        height: '50px',
                                        borderRadius: '50%',
                                        backgroundColor: 'transparent',
                                    }}
                                >
                                </button>
                            ) : (
                                <div style={{ width: '50px', height: '50px' }}></div> // 빈 칸 처리
                            )}
                        </td>
                    ))}
                </tr>
            ))}
            </tbody>
        </table>
    );
}
