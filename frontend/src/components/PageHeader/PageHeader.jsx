import "./PageHeader.css";

function PageHeader({ titulo, descripcion }) {

    return (
        <section className="page-header">
            <div className="page-header-content">
                <h1>{titulo}</h1>
                <p>{descripcion}</p>
            </div>
        </section>
    );
}

export default PageHeader;