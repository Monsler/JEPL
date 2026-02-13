use jepl_lexer::Lexer;
use jepl_parser::Parser;

fn main() {
    let mut lexer = Lexer::new("[
    {
    \"name\": \"print\",
    \"args\": [\"hello\", \"world\"]
    },
    {
    \"name\": \"print\",
    \"args\": [\"hho\", \"wzzzd\"]
    }
    ]
    ");
    let array = lexer.tokenize();

    let mut parser= Parser::new(array);
    let value = parser.parse();

    value.iter().for_each(|cmd| println!("{:?}", cmd));
}
