pub mod command;
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
                panic!("Error parsing. Got {:?}; Awaited {:?} (pos {})", token.token_type, token_type, token.position)
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

        if let Some(token) = self.current() {
            if token.token_type == TokenType::RBRACE {
                self.expect(TokenType::RBRACE);
                return;
            }
        }

        loop {
            let key = match self.current() {
                Some(t) if t.token_type == TokenType::STR => {
                    t.value
                }

                Some(t) => {
                    panic!("Expected STR; Got {:?}", t.token_type)
                }

                None => {
                    panic!("Got EOF")
                }
            };

            self.advice();

            self.expect(TokenType::COLON);

            match key {
                "name" => {
                    command_type = self.current().unwrap().value;
                    self.expect(TokenType::STR);
                }

                "args" => {
                    args = self.parse_array();
                }

                _ => {
                    panic!("Unexpected key: {}", key)
                }
            }

            match self.current() {
                Some(t) if t.token_type == TokenType::COMMA => {
                    let pos = t.position;
                    self.advice();

                    if let Some(token) = self.current() {
                        if token.token_type == TokenType::RBRACE {
                            panic!("Unexpected COMMA at pos {}", pos)
                        }
                    } 
                }

                Some(t) if t.token_type == TokenType::RBRACE => {
                    break;
                }

                Some(t) => {
                    panic!("Expected COMMA or RBRACE; Got {:?} at pos {}", t.token_type, t.position)
                }

                None => {
                    panic!("Unexpected EOF")
                }
            }
        }

        self.expect(TokenType::RBRACE);
      
        match command_type {
            "print" => self.commands.push(Command::CommandPrint(args)),
            
            "println" => self.commands.push(Command::CommandPrintln(args)),

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

                TokenType::COMMA => {
                    self.advice();
                    if let Some(next_token) = self.current() {
                        if next_token.token_type == TokenType::RBRACKET {
                            panic!("Unexpected COMMA; Awaited RBRACKET")
                        }
                    }
                    
                }

                _ => {
                    panic!("Unexpected token: {:?} ({:?})", token.token_type, token.value)
                }
            }
        }

        
        if let Some(next_token) = self.current() {
            if next_token.token_type != TokenType::RBRACKET && next_token.token_type != TokenType::COMMA {
                panic!("Expected {:?} or {:?}; Got {:?}", TokenType::RBRACKET, TokenType::COMMA, next_token.token_type)
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

            match self.current() {
                Some(t) if t.token_type == TokenType::COMMA => {
                    if let Some(n_token) = self.peek() {
                        if n_token.token_type == TokenType::RBRACKET {
                            panic!("Unexpected COMMA at pos {}", t.position)
                        }
                    }
                    self.advice();
                }
                Some(t) if t.token_type == TokenType::RBRACKET => {
                    break;
                }
                Some(t) => {
                    panic!(
                        "Expected COMMA or RBRACKET, got {:?} at pos {}",
                        t.token_type, t.position
                    );
                }
                None => break,
            }
        }

        self.expect(TokenType::RBRACKET);
        &self.commands
    }

}