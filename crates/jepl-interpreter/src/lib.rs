use std::collections::HashMap;

use jepl_parser::command::Command;

pub struct Interpreter<'a> {
    commands: &'a Vec<Command<'a>>,
    variables: HashMap<&'a str, &'a str>
}

impl<'a> Interpreter<'a> {
    pub fn new(commands: &'a Vec<Command<'a>>) -> Self {
        Self { commands, variables: HashMap::new() }
    }

    pub fn run(&mut self) {
        for command in self.commands {
            match command {
                Command::CommandPrint(args) => {
                    args
                    .iter()
                    .for_each(|arg| print!("{} ", arg));
                }

                Command::CommandPrintln(args) => {
                    args
                    .iter()
                    .for_each(|arg| print!("{} ", arg));
                    println!()
                }
            }
        }
    } 
}