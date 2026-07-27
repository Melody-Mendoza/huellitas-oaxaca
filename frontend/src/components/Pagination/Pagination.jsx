import "./Pagination.css";
function Pagination(){
    return(
        <nav className="pagination">
            <button>{"<"}</button>
            <button className="active">
                1
            </button>
            <button>2</button>
            <button>3</button>
            <span>...</span>
            <button>8</button>
            <button>{">"}</button>
        </nav>

    );

}

export default Pagination;