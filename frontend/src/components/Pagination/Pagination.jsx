import "./Pagination.css";

const MAX_VISIBLE_PAGES = 5;

function getVisiblePages(currentPage, totalPages) {
    const halfWindow = Math.floor(MAX_VISIBLE_PAGES / 2);
    let start = Math.max(0, currentPage - halfWindow);
    const end = Math.min(
        totalPages,
        start + MAX_VISIBLE_PAGES
    );

    start = Math.max(0, end - MAX_VISIBLE_PAGES);

    return Array.from(
        { length: end - start },
        (_, index) => start + index
    );
}

function Pagination({
    currentPage = 0,
    totalPages = 0,
    first = true,
    last = true,
    onPageChange = () => {},
    disabled = false
}) {
    if (totalPages <= 1) {
        return null;
    }

    const visiblePages = getVisiblePages(
        currentPage,
        totalPages
    );

    const changePage = (nextPage) => {
        if (
            disabled
            || nextPage < 0
            || nextPage >= totalPages
            || nextPage === currentPage
        ) {
            return;
        }

        onPageChange(nextPage);
    };

    return (
        <nav
            className="pagination"
            aria-label="Paginación del catálogo"
        >
            <button
                type="button"
                onClick={() => {
                    changePage(currentPage - 1);
                }}
                disabled={disabled || first}
                aria-label="Página anterior"
            >
                {"<"}
            </button>

            {visiblePages.map((pageNumber) => (
                <button
                    key={pageNumber}
                    type="button"
                    className={
                        pageNumber === currentPage
                            ? "active"
                            : ""
                    }
                    aria-current={
                        pageNumber === currentPage
                            ? "page"
                            : undefined
                    }
                    onClick={() => {
                        changePage(pageNumber);
                    }}
                    disabled={disabled}
                >
                    {pageNumber + 1}
                </button>
            ))}

            <button
                type="button"
                onClick={() => {
                    changePage(currentPage + 1);
                }}
                disabled={disabled || last}
                aria-label="Página siguiente"
            >
                {">"}
            </button>
        </nav>
    );
}

export default Pagination;
