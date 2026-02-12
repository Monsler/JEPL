mod token;

use crate::token::*;

pub struct Lexer<'a> {
    input_chars: std::str::CharIndices<'a>,
    input: &'a str,
    current_char: Option<(usize, char)>,
    tokens: Vec<Token<'a>>
}

impl<'a> Lexer<'a> {
    pub fn new(source: &'a str) -> Self {
        let mut input_chars = source.char_indices();
        let current_char = input_chars.next();
        
        Self {
            input_chars: input_chars,
            input: source,
            current_char: current_char,
            tokens: vec![]
        }
    }

    fn advice(&mut self) {
        self.current_char = self.input_chars.next()
    }

    fn push_current_char(&mut self, token_type: TokenType) {
        if let Some((idx, sym)) = self.current_char {
            let start = idx;
            let end = idx + sym.len_utf8();

            self.push_slice(start, end, token_type);
            self.advice();
        }        
    }

    fn get_cursor_pos(&mut self) -> usize {
        self.current_char.map_or(self.input.len(), |(idx, _)| idx)
    }

    fn push_slice(&mut self, start: usize, end: usize, token_type: TokenType) {
        self.tokens.push(Token { value: &self.input[start..end], position: start, token_type })
    }

    fn push_number(&mut self) {
        let mut buf = String::new();
        let start = self.get_cursor_pos();

        while let Some((_, current)) = self.current_char && current.is_numeric() {
            buf.push(current);
            self.advice();
        }

        let end = self.get_cursor_pos();
        self.push_slice(start, end, TokenType::NUMBER);
    }

    fn push_str(&mut self, delimiter: char) {
        let mut buf = String::new();
        let start = self.get_cursor_pos();

        while let Some((_, current)) = self.current_char && current != delimiter {
            self.advice();
            buf.push(current); 
        }
        
        let end = self.get_cursor_pos();

        self.advice();

        self.push_slice(start, end, TokenType::STR);
    } 

    fn skip_whitespaces(&mut self) {
         while let Some((_, current)) = self.current_char && current == ' '{
            self.advice();
        }
    }

    pub fn tokenize(&mut self) -> &Vec<Token<'a>> {
        while let Some((_, current)) = self.current_char {
            if current == ' ' {
                self.skip_whitespaces();
            } else if current == ']' {
                self.push_current_char(TokenType::RBRACKET);
            } else if current == '[' {
                self.push_current_char(TokenType::LBRACKET);
            } else if current == '}' {
                self.push_current_char(TokenType::RBRACE);
            } else if current == '{' {
                self.push_current_char(TokenType::LBRACE);
            } else if current == ':' {
                self.push_current_char(TokenType::COLON);
            } else if current == ',' {
                self.push_current_char(TokenType::COMMA);
            } else if current.is_numeric() {
                self.push_number();
            } else if current == '"' || current == '\'' {
                self.advice();
                self.push_str(current);
            } else {
                println!("WARNING!!! Unknown symbol: <{}> at pos {}; skipping it", current, 0);
                self.advice();
            }
        }
        return &self.tokens;
    }
}