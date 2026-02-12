use std::time::Instant;

use jepl_lexer::Lexer;

fn main() {
    let time = Instant::now();
    let mut lexer = Lexer::new("[{\"13\": \"123\",\"41\": \"123\", \"elem\": 123}]");
    let array = lexer.tokenize();

    for elem in array {
        println!("{}", elem)
    }

    println!("Time took to tokenize: {} ms.", time.elapsed().as_millis())
}
