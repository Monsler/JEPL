use std::{path::Path, process::exit};

use clap::{Arg, Command};
use jepl_interpreter::Interpreter;
use jepl_lexer::Lexer;
use jepl_parser::Parser;

fn run(source: String) {
    let mut lexer = Lexer::new(&source);
    let array = lexer.tokenize();

    let mut parser= Parser::new(array);
    let commands = parser.parse();

    let mut interpreter = Interpreter::new(commands);
    interpreter.run();
}

fn main() {
    let arg = Command::new("jepl")
        .about("small json language")
        .subcommand(Command::new("run")
            .short_flag('r')
            .long_flag("run")
            .about("run .json file")
            .arg(Arg::new("file")))
    .get_matches();

    match arg.subcommand() {
        Some(("run", matches)) => {
            let file = matches.get_one::<String>("file").map(|s| s.as_str());

            if let Some(path) = file {
                let path = Path::new(path);

                if path.exists() {
                    let file = std::fs::read_to_string(path);

                    match file {
                        Ok(source) => {
                            run(source);
                            exit(0)
                        },
                        Err(e) => {
                            panic!("{}", e);
                        }
                    }
                } else {
                    panic!("File <{}> doesn't exist!", path.to_str().unwrap())
                }
            }
        },

        _ => {}
    }
    
}
