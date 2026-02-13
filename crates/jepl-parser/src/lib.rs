mod command;
use crate::command::*;
use jepl_lexer::token::*;

pub struct Parser<'a> {
    commands: Vec<Command<'a>>,
    tokens: &'a [Token<'a>],
    position: usize,
}

impl<'a> Parser<'a> {
    pub fn new(tokens: &'a [Token<'a>]) -> Self {
        Self { commands: Vec::new(), tokens, position: 0 }
    }

    fn current(&self) -> Option<&Token<'a>> {
        self.tokens.get(self.position)
    }

    fn peek(&self) -> Option<&Token<'a>> {
        self.tokens.get(self.position+1)
    }

    fn advice(&mut self) {
        if self.position < self.tokens.len() {
            self.position += 1;
        }
    }

    fn expect(&mut self, token_type: TokenType) {
        match self.current() {
            Some(token) if token.token_type == token_type => {
                self.advice()
            }
            Some(token) => {
                panic!("Error parsing. Got {:?}; Awaited {:?} (pos {})", token_type, token.token_type, token.position)
            }
            None => {
                panic!("Error parsing. Reached EOF")
            }
        }
    }

    fn parse_command(&mut self) {
        self.expect(TokenType::LBRACE);

        let mut args: Vec<&'a str> = Vec::new();
        let mut command_type = "";

        while let Some(token) = self.current() {
            if token.token_type == TokenType::RBRACE { break; }
            match token.value {
                "name" => {
                    self.expect(TokenType::STR);
                    self.expect(TokenType::COLON);
                    command_type = self.current().unwrap().value;
                    self.advice();
                }

                "args" => {
                    self.expect(TokenType::STR);
                    self.expect(TokenType::COLON);
                    args = self.parse_array();
                },

                _ => {
                    self.advice();
                }
            }
        }

        self.expect(TokenType::RBRACE);

        if let Some(next_token) = self.peek() {
            if next_token.token_type != TokenType::RBRACE {
                self.expect(TokenType::COMMA);
            }
        }

        match command_type {
            "print" => {
                self.commands.push(Command::CommandPrint(args));
            },

            _ => {
                // TODO check for user-defined functions
                panic!("Undefined function: {}", command_type)
            }
        }
    }

    fn parse_array(&mut self) -> Vec<&'a str> {
        let mut out: Vec<&'a str> = Vec::new();

        self.expect(TokenType::LBRACKET);

        while let Some(token) = self.current() {
            if token.token_type == TokenType::RBRACKET { break; }

            match token.token_type {
                TokenType::STR | TokenType::NUMBER => {
                    out.push(token.value);
                    self.advice();
                }

                _ => {
                    panic!("Unexpected token: {:?} ({:?})", token.token_type, token.value)
                }
            }

            if let Some(next_token) = self.current() {
                if next_token.token_type != TokenType::RBRACKET {
                    self.expect(TokenType::COMMA);
                }
            }
        }

        self.expect(TokenType::RBRACKET);

        out
    }

    pub fn parse(&mut self) -> &Vec<Command<'a>> {
        self.expect(TokenType::LBRACKET);
        while let Some(token) = self.current() {
            if token.token_type == TokenType::RBRACKET {
                break;
            }

            self.parse_command();
        }

        &self.commands
    }
}